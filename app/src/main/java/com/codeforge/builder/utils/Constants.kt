package com.codeforge.builder.utils

object Constants {

    // GitHub API
    const val GITHUB_BASE_URL = "https://api.github.com/"
    const val GITHUB_API_VERSION = "2022-11-28"
    const val GITHUB_ACCEPT_HEADER = "application/vnd.github+json"

    // Build Status
    const val BUILD_STATUS_QUEUED = "queued"
    const val BUILD_STATUS_IN_PROGRESS = "in_progress"
    const val BUILD_STATUS_COMPLETED = "completed"
    const val BUILD_STATUS_WAITING = "waiting"

    // Build Conclusion
    const val BUILD_CONCLUSION_SUCCESS = "success"
    const val BUILD_CONCLUSION_FAILURE = "failure"
    const val BUILD_CONCLUSION_CANCELLED = "cancelled"
    const val BUILD_CONCLUSION_SKIPPED = "skipped"

    // Project Types
    const val PROJECT_TYPE_HTML = "HTML/CSS/JS"
    const val PROJECT_TYPE_KOTLIN = "Kotlin"
    const val PROJECT_TYPE_JAVA = "Java"

    // File Extensions
    const val EXT_HTML = ".html"
    const val EXT_CSS = ".css"
    const val EXT_JS = ".js"
    const val EXT_KT = ".kt"
    const val EXT_JAVA = ".java"
    const val EXT_XML = ".xml"
    const val EXT_JSON = ".json"
    const val EXT_APK = ".apk"

    // Database
    const val DATABASE_NAME = "codeforge_db"
    const val DATABASE_VERSION = 1

    // WorkManager
    const val WORK_TAG_BUILD_MONITOR = "build_monitor"
    const val WORK_DATA_RUN_ID = "run_id"
    const val WORK_DATA_REPO_OWNER = "repo_owner"
    const val WORK_DATA_REPO_NAME = "repo_name"
    const val WORK_DATA_BUILD_RECORD_ID = "build_record_id"
    const val WORK_POLL_INTERVAL_SECONDS = 15L

    // DataStore Keys
    const val PREF_GITHUB_TOKEN = "github_token"
    const val PREF_GITHUB_USERNAME = "github_username"
    const val PREF_GITHUB_EMAIL = "github_email"
    const val PREF_THEME_MODE = "theme_mode"
    const val PREF_EDITOR_FONT_SIZE = "editor_font_size"
    const val PREF_EDITOR_THEME = "editor_theme"
    const val PREF_DEFAULT_PROJECT_TYPE = "default_project_type"

    // Editor themes
    const val EDITOR_THEME_DARK = "dark"
    const val EDITOR_THEME_LIGHT = "light"
    const val EDITOR_THEME_MONOKAI = "monokai"

    // Default values
    const val DEFAULT_FONT_SIZE = 14
    const val MIN_FONT_SIZE = 10
    const val MAX_FONT_SIZE = 24

    // Artifact name in GitHub Actions (must match workflow)
    const val ARTIFACT_NAME_PREFIX = "app-debug"

    // Generated project defaults
    const val GENERATED_PACKAGE_PREFIX = "com.codeforge.generated"
    const val GENERATED_APP_MIN_SDK = 26
    const val GENERATED_APP_TARGET_SDK = 34
    const val GENERATED_GRADLE_VERSION = "8.6"
    const val GENERATED_AGP_VERSION = "8.3.2"
    const val GENERATED_KOTLIN_VERSION = "1.9.23"

    // Timeouts
    const val NETWORK_CONNECT_TIMEOUT = 30L
    const val NETWORK_READ_TIMEOUT = 60L
    const val NETWORK_WRITE_TIMEOUT = 60L

    // Max retries for build polling
    const val MAX_POLL_RETRIES = 80 // 80 * 15s = 20 minutes max
}
