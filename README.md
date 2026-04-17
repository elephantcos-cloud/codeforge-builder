# CodeForge — Android Code to APK Builder

**CodeForge** is a professional Android app that lets you write HTML/CSS/JS, Kotlin, or Java code directly on your phone — then push to GitHub and automatically build an APK via GitHub Actions.

---

## Features

- **Multi-language Editor** — HTML, CSS, JavaScript, Kotlin, Java, XML with syntax highlighting
- **Autocomplete** — keyword suggestions for all supported languages
- **File Manager** — multiple files per project, add/delete files
- **Template Library** — 11 ready-made app templates (Calculator, Todo, Quiz, Clock, Counter, Notepad, and more)
- **GitHub Integration** — auto-creates repo, pushes all project files
- **Build History** — tracks all builds with status, duration, APK size
- **APK Preview** — view APK info and install directly
- **Background Monitoring** — WorkManager polls GitHub Actions every 15s
- **Push Notifications** — notified when build succeeds or fails
- **Settings** — GitHub token, editor theme, font size, app theme

---

## Setup (Termux)

### 1. Extract all ZIPs into one folder

```bash
# In Termux:
mkdir codeforge && cd codeforge

# Extract each zip (repeat for all 7):
unzip ~/storage/downloads/codeforge_zip1_gradle_cicd.zip
unzip ~/storage/downloads/codeforge_zip2_data_layer.zip
unzip ~/storage/downloads/codeforge_zip3_ui_kotlin.zip
unzip ~/storage/downloads/codeforge_zip4_layouts.zip
unzip ~/storage/downloads/codeforge_zip5_resources.zip
unzip ~/storage/downloads/codeforge_zip6_editor_asset.zip
unzip ~/storage/downloads/codeforge_zip7_final.zip
```

### 2. Create GitHub repository

1. Go to [github.com/new](https://github.com/new)
2. Create a public repo named `CodeForge`
3. Do NOT initialize with README (we push our own)

### 3. Get GitHub Personal Access Token

1. Go to [github.com/settings/tokens/new](https://github.com/settings/tokens/new)
2. Required scopes: **repo**, **workflow**
3. Copy the token

### 4. Push to GitHub

```bash
cd codeforge
chmod +x push_to_github.sh setup.sh
bash setup.sh       # Downloads gradle-wrapper.jar
bash push_to_github.sh https://github.com/YOUR_USERNAME/CodeForge
```

Enter your GitHub token when asked for password.

### 5. Monitor Build

- Go to: `https://github.com/YOUR_USERNAME/CodeForge/actions`
- Build takes **3–8 minutes**
- Download APK from Actions → Artifacts

---

## Project Structure

```
codeforge/
├── .github/workflows/build.yml     ← GitHub Actions CI/CD
├── app/
│   ├── src/main/
│   │   ├── assets/editor/          ← Code editor HTML
│   │   ├── java/com/codeforge/
│   │   │   ├── data/               ← Room DB, Retrofit, Repository
│   │   │   ├── ui/                 ← Fragments + ViewModels
│   │   │   └── worker/             ← Build monitor WorkManager
│   │   └── res/                    ← Layouts, drawables, navigation
│   └── build.gradle.kts
├── push_to_github.sh               ← Termux push script
└── setup.sh                        ← First-time setup
```

---

## Architecture

- **MVVM** architecture with LiveData
- **Room** for local project/file/build storage
- **Retrofit + OkHttp** for GitHub API
- **DataStore** for encrypted preferences
- **WorkManager** for background build polling
- **Navigation Component** for fragment routing
- **Material Design 3** UI

---

## How It Works

```
You write code in CodeForge
        ↓
Tap "Build APK"
        ↓
CodeForge generates full Android project files
        ↓
Pushes to GitHub via API
        ↓
GitHub Actions starts workflow
        ↓
WorkManager polls status every 15s
        ↓
On success: notification + APK download available
        ↓
Download & install APK directly from app
```

---

*Built with ❤️ by CodeForge*
