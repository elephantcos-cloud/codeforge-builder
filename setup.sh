#!/bin/bash
# =============================================
#  CodeForge - Setup Script for Termux
#  Run this ONCE before pushing to GitHub
# =============================================

echo "🚀 CodeForge Setup Script"
echo "========================="

# Download Gradle Wrapper JAR (required for ./gradlew to work)
WRAPPER_DIR="gradle/wrapper"
JAR_FILE="$WRAPPER_DIR/gradle-wrapper.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "📥 Downloading Gradle Wrapper JAR..."
    mkdir -p "$WRAPPER_DIR"
    curl -L -o "$JAR_FILE" \
        "https://github.com/gradle/gradle/raw/v8.6.0/gradle/wrapper/gradle-wrapper.jar"
    echo "✅ Gradle Wrapper JAR downloaded"
else
    echo "✅ Gradle Wrapper JAR already exists"
fi

# Make gradlew executable
chmod +x gradlew
echo "✅ gradlew is now executable"

# Check Git
if command -v git &> /dev/null; then
    echo "✅ Git is available"
else
    echo "❌ Git not found. Install: pkg install git"
    exit 1
fi

echo ""
echo "✅ Setup complete! Now run:"
echo "   git init"
echo "   git add ."
echo "   git commit -m 'Initial commit: CodeForge APK Builder'"
echo "   git remote add origin YOUR_GITHUB_REPO_URL"
echo "   git push -u origin main"
echo ""
echo "GitHub Actions will automatically build your APK! 🎉"
