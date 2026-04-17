package com.codeforge.builder.utils

import com.codeforge.builder.data.local.entity.Project
import com.codeforge.builder.data.local.entity.ProjectFile

object ProjectGenerator {

    // ── Generate all files needed for a GitHub push ────────────
    fun generateProjectFiles(
        project: Project,
        userFiles: List<ProjectFile>
    ): List<Pair<String, String>> {
        val files = mutableListOf<Pair<String, String>>()

        when (project.type) {
            Constants.PROJECT_TYPE_HTML -> {
                files.addAll(generateWebViewProject(project, userFiles))
            }
            Constants.PROJECT_TYPE_KOTLIN, Constants.PROJECT_TYPE_JAVA -> {
                files.addAll(generateNativeProject(project, userFiles))
            }
        }

        // Common files for all project types
        files.add(".github/workflows/build.yml" to generateBuildWorkflow(project))
        files.add("README.md" to generateReadme(project))

        return files
    }

    // ── WebView (HTML/JS/CSS) Project ─────────────────────────
    private fun generateWebViewProject(
        project: Project,
        userFiles: List<ProjectFile>
    ): List<Pair<String, String>> {
        val files = mutableListOf<Pair<String, String>>()
        val pkg = project.packageName
        val pkgPath = pkg.replace('.', '/')
        val appName = project.appName
        val minSdk = Constants.GENERATED_APP_MIN_SDK

        // Root build files
        files.add("build.gradle.kts" to generateRootBuildGradle())
        files.add("settings.gradle.kts" to generateSettings(project.githubRepoName))
        files.add("gradle.properties" to generateGradleProperties())
        files.add("gradle/wrapper/gradle-wrapper.properties" to generateWrapperProps())

        // App build.gradle
        files.add("app/build.gradle.kts" to generateWebViewAppBuildGradle(pkg, minSdk))

        // Manifest
        files.add("app/src/main/AndroidManifest.xml" to generateWebViewManifest(pkg, appName))

        // MainActivity.kt
        files.add("app/src/main/java/$pkgPath/MainActivity.kt" to generateWebViewMainActivity(pkg))

        // User HTML/JS/CSS files go into assets
        for (userFile in userFiles) {
            files.add("app/src/main/assets/${userFile.fileName}" to userFile.content)
        }

        // Default index.html if none provided
        if (userFiles.none { it.fileName.endsWith(".html") }) {
            files.add("app/src/main/assets/index.html" to generateDefaultHtml(appName))
        }

        // Strings resource
        files.add("app/src/main/res/values/strings.xml" to generateStrings(appName))

        // Themes
        files.add("app/src/main/res/values/themes.xml" to generateThemes(pkg))
        files.add("app/src/main/res/values/colors.xml" to generateColors())

        // App icon (simple vector)
        files.add("app/src/main/res/drawable/ic_launcher_background.xml" to generateIconBackground())
        files.add("app/src/main/res/drawable/ic_launcher_foreground.xml" to generateIconForeground(appName))
        files.add("app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml" to generateAdaptiveIcon())

        // Proguard
        files.add("app/proguard-rules.pro" to "# Add project specific ProGuard rules here.\n-keepattributes *Annotation*\n")

        return files
    }

    // ── Native (Kotlin/Java) Project ──────────────────────────
    private fun generateNativeProject(
        project: Project,
        userFiles: List<ProjectFile>
    ): List<Pair<String, String>> {
        val files = mutableListOf<Pair<String, String>>()
        val pkg = project.packageName
        val pkgPath = pkg.replace('.', '/')
        val appName = project.appName
        val isKotlin = project.type == Constants.PROJECT_TYPE_KOTLIN
        val langDir = if (isKotlin) "kotlin" else "java"
        val minSdk = Constants.GENERATED_APP_MIN_SDK

        // Root build files
        files.add("build.gradle.kts" to generateRootBuildGradle())
        files.add("settings.gradle.kts" to generateSettings(project.githubRepoName))
        files.add("gradle.properties" to generateGradleProperties())
        files.add("gradle/wrapper/gradle-wrapper.properties" to generateWrapperProps())

        // App build.gradle
        files.add("app/build.gradle.kts" to generateNativeAppBuildGradle(pkg, minSdk, isKotlin))

        // Manifest
        files.add("app/src/main/AndroidManifest.xml" to generateNativeManifest(pkg, appName))

        // User source files
        for (userFile in userFiles) {
            val srcPath = "app/src/main/$langDir/$pkgPath/${userFile.fileName}"
            files.add(srcPath to userFile.content)
        }

        // Default MainActivity if none provided
        if (userFiles.none { it.fileName == "MainActivity.kt" || it.fileName == "MainActivity.java" }) {
            if (isKotlin) {
                files.add("app/src/main/kotlin/$pkgPath/MainActivity.kt" to generateDefaultKotlinActivity(pkg, appName))
            } else {
                files.add("app/src/main/java/$pkgPath/MainActivity.java" to generateDefaultJavaActivity(pkg, appName))
            }
        }

        // Default layout
        files.add("app/src/main/res/layout/activity_main.xml" to generateDefaultLayout(appName))

        // Resources
        files.add("app/src/main/res/values/strings.xml" to generateStrings(appName))
        files.add("app/src/main/res/values/themes.xml" to generateThemes(pkg))
        files.add("app/src/main/res/values/colors.xml" to generateColors())
        files.add("app/src/main/res/drawable/ic_launcher_background.xml" to generateIconBackground())
        files.add("app/src/main/res/drawable/ic_launcher_foreground.xml" to generateIconForeground(appName))
        files.add("app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml" to generateAdaptiveIcon())
        files.add("app/proguard-rules.pro" to "# Add project specific ProGuard rules here.\n-keepattributes *Annotation*\n")

        return files
    }

    // ── GitHub Actions Workflow ────────────────────────────────
    private fun generateBuildWorkflow(project: Project): String = """
name: Build APK

on:
  push:
    branches: [ main, master ]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Cache Gradle
        uses: actions/cache@v4
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: ${'$'}{{ runner.os }}-gradle-${'$'}{{ hashFiles('**/*.gradle*', '**/gradle-wrapper.properties') }}
      - name: Grant execute permission
        run: chmod +x gradlew
      - name: Download Gradle Wrapper JAR
        run: |
          mkdir -p gradle/wrapper
          curl -L -o gradle/wrapper/gradle-wrapper.jar \
            https://github.com/gradle/gradle/raw/v${Constants.GENERATED_GRADLE_VERSION}.0/gradle/wrapper/gradle-wrapper.jar
      - name: Build Debug APK
        run: ./gradlew assembleDebug --no-daemon
      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: app-debug
          path: app/build/outputs/apk/debug/app-debug.apk
          retention-days: 7
""".trimIndent()

    // ── Template file generators ───────────────────────────────

    private fun generateRootBuildGradle() = """
plugins {
    id("com.android.application") version "${Constants.GENERATED_AGP_VERSION}" apply false
    id("org.jetbrains.kotlin.android") version "${Constants.GENERATED_KOTLIN_VERSION}" apply false
}
""".trimIndent()

    private fun generateSettings(repoName: String) = """
pluginManagement {
    repositories { google(); mavenCentral(); gradlePluginPortal() }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories { google(); mavenCentral() }
}
rootProject.name = "$repoName"
include(":app")
""".trimIndent()

    private fun generateGradleProperties() = """
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
kotlin.code.style=official
android.nonTransitiveRClass=true
""".trimIndent()

    private fun generateWrapperProps() = """
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-${Constants.GENERATED_GRADLE_VERSION}-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
""".trimIndent()

    private fun generateWebViewAppBuildGradle(pkg: String, minSdk: Int) = """
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "$pkg"
    compileSdk = ${Constants.GENERATED_APP_TARGET_SDK}
    defaultConfig {
        applicationId = "$pkg"
        minSdk = $minSdk
        targetSdk = ${Constants.GENERATED_APP_TARGET_SDK}
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        release { isMinifyEnabled = false }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
    buildFeatures { viewBinding = true }
}
dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.webkit:webkit:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}
""".trimIndent()

    private fun generateNativeAppBuildGradle(pkg: String, minSdk: Int, isKotlin: Boolean): String {
        val kotlinPlugin = if (isKotlin) "\n    id(\"org.jetbrains.kotlin.android\")" else ""
        val kotlinDep = if (isKotlin) "\n    implementation(\"androidx.core:core-ktx:1.12.0\")" else ""
        val kotlinOptions = if (isKotlin) "\n    kotlinOptions { jvmTarget = \"17\" }" else ""
        return """
plugins {
    id("com.android.application")$kotlinPlugin
}
android {
    namespace = "$pkg"
    compileSdk = ${Constants.GENERATED_APP_TARGET_SDK}
    defaultConfig {
        applicationId = "$pkg"
        minSdk = $minSdk
        targetSdk = ${Constants.GENERATED_APP_TARGET_SDK}
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes { release { isMinifyEnabled = false } }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }$kotlinOptions
    buildFeatures { viewBinding = true }
}
dependencies {$kotlinDep
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
}
""".trimIndent()
    }

    private fun generateWebViewManifest(pkg: String, appName: String) = """
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <application
        android:label="$appName"
        android:icon="@mipmap/ic_launcher"
        android:theme="@style/Theme.App"
        android:usesCleartextTraffic="true">
        <activity android:name=".MainActivity" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>
    </application>
</manifest>
""".trimIndent()

    private fun generateNativeManifest(pkg: String, appName: String) = """
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <uses-permission android:name="android.permission.INTERNET"/>
    <application
        android:label="$appName"
        android:icon="@mipmap/ic_launcher"
        android:theme="@style/Theme.App">
        <activity android:name=".MainActivity" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>
                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>
    </application>
</manifest>
""".trimIndent()

    private fun generateWebViewMainActivity(pkg: String) = """
package $pkg

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        setContentView(webView)
        webView.apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = true
                cacheMode = WebSettings.LOAD_DEFAULT
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            loadUrl("file:///android_asset/index.html")
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) webView.goBack()
        else super.onBackPressed()
    }
}
""".trimIndent()

    private fun generateDefaultKotlinActivity(pkg: String, appName: String) = """
package $pkg

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import android.widget.LinearLayout
import android.view.Gravity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(0xFF121212.toInt())
        }
        val title = MaterialTextView(this).apply {
            text = "$appName"
            textSize = 28f
            setTextColor(0xFFFFFFFF.toInt())
            gravity = Gravity.CENTER
            setPadding(32, 32, 32, 16)
        }
        val subtitle = MaterialTextView(this).apply {
            text = "Built with CodeForge"
            textSize = 16f
            setTextColor(0xFF9E9E9E.toInt())
            gravity = Gravity.CENTER
            setPadding(32, 0, 32, 48)
        }
        layout.addView(title)
        layout.addView(subtitle)
        setContentView(layout)
    }
}
""".trimIndent()

    private fun generateDefaultJavaActivity(pkg: String, appName: String) = """
package $pkg;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setBackgroundColor(0xFF121212);
        TextView title = new TextView(this);
        title.setText("$appName");
        title.setTextSize(28);
        title.setTextColor(0xFFFFFFFF);
        title.setGravity(Gravity.CENTER);
        title.setPadding(32,32,32,16);
        TextView sub = new TextView(this);
        sub.setText("Built with CodeForge");
        sub.setTextSize(16);
        sub.setTextColor(0xFF9E9E9E);
        sub.setGravity(Gravity.CENTER);
        layout.addView(title);
        layout.addView(sub);
        setContentView(layout);
    }
}
""".trimIndent()

    private fun generateDefaultLayout(appName: String) = """
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:gravity="center"
    android:background="#121212">
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="$appName"
        android:textSize="28sp"
        android:textColor="#FFFFFF"/>
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Built with CodeForge"
        android:textSize="16sp"
        android:textColor="#9E9E9E"
        android:layout_marginTop="8dp"/>
</LinearLayout>
""".trimIndent()

    private fun generateDefaultHtml(appName: String) = """
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>$appName</title>
<style>
  * { margin: 0; padding: 0; box-sizing: border-box; }
  body { background: #121212; color: #fff; font-family: sans-serif;
         display: flex; flex-direction: column; align-items: center;
         justify-content: center; min-height: 100vh; text-align: center; padding: 24px; }
  h1 { font-size: 2rem; margin-bottom: 12px; color: #BB86FC; }
  p  { font-size: 1rem; color: #9E9E9E; }
</style>
</head>
<body>
  <h1>$appName</h1>
  <p>Built with CodeForge 🚀</p>
</body>
</html>
""".trimIndent()

    private fun generateStrings(appName: String) = """
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">$appName</string>
</resources>
""".trimIndent()

    private fun generateThemes(pkg: String) = """
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.App" parent="Theme.MaterialComponents.DayNight.DarkActionBar">
        <item name="colorPrimary">@color/purple_500</item>
        <item name="colorPrimaryVariant">@color/purple_700</item>
        <item name="colorOnPrimary">@color/white</item>
        <item name="colorSecondary">@color/teal_200</item>
        <item name="colorSecondaryVariant">@color/teal_700</item>
        <item name="colorOnSecondary">@color/black</item>
        <item name="statusBarColor">?attr/colorPrimaryVariant</item>
    </style>
</resources>
""".trimIndent()

    private fun generateColors() = """
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="purple_200">#FFBB86FC</color>
    <color name="purple_500">#FF6200EE</color>
    <color name="purple_700">#FF3700B3</color>
    <color name="teal_200">#FF03DAC5</color>
    <color name="teal_700">#FF018786</color>
    <color name="black">#FF000000</color>
    <color name="white">#FFFFFFFF</color>
</resources>
""".trimIndent()

    private fun generateIconBackground() = """
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp" android:height="108dp" android:viewportWidth="108" android:viewportHeight="108">
    <path android:fillColor="#6200EE" android:pathData="M0,0h108v108h-108z"/>
</vector>
""".trimIndent()

    private fun generateIconForeground(appName: String): String {
        val letter = appName.firstOrNull()?.uppercaseChar() ?: 'A'
        return """
<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp" android:height="108dp" android:viewportWidth="108" android:viewportHeight="108">
    <text android:fillColor="#FFFFFF" android:fontFamily="sans-serif-medium"
        android:textSize="60" android:x="30" android:y="75">$letter</text>
</vector>
""".trimIndent()
    }

    private fun generateAdaptiveIcon() = """
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
""".trimIndent()

    private fun generateReadme(project: Project) = """
# ${project.appName}

${project.description.ifEmpty { "An Android app built with CodeForge." }}

## Build Status
[![Build APK](https://github.com/USERNAME/${project.githubRepoName}/actions/workflows/build.yml/badge.svg)](https://github.com/USERNAME/${project.githubRepoName}/actions)

## Type
**${project.type}** Android Application

## Package
`${project.packageName}`

---
*Built with [CodeForge](https://github.com/codeforge)*
""".trimIndent()
}
