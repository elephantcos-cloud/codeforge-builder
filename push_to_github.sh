#!/bin/bash
# =====================================================
#  CodeForge — Termux GitHub Push Script
#  Run from project root: bash push_to_github.sh
# =====================================================

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m'

echo -e "${BOLD}${CYAN}"
echo "  ██████╗ ██████╗ ██████╗ ███████╗"
echo " ██╔════╝██╔═══██╗██╔══██╗██╔════╝"
echo " ██║     ██║   ██║██║  ██║█████╗  "
echo " ██║     ██║   ██║██║  ██║██╔══╝  "
echo " ╚██████╗╚██████╔╝██████╔╝███████╗"
echo "  ╚═════╝ ╚═════╝ ╚═════╝ ╚══════╝"
echo "  ███████╗ ██████╗ ██████╗  ██████╗███████╗"
echo "  ██╔════╝██╔═══██╗██╔══██╗██╔════╝██╔════╝"
echo "  █████╗  ██║   ██║██████╔╝██║  ███╗█████╗"
echo "  ██╔══╝  ██║   ██║██╔══██╗██║   ██║██╔══╝"
echo "  ██║     ╚██████╔╝██║  ██║╚██████╔╝███████╗"
echo "  ╚═╝      ╚═════╝ ╚═╝  ╚═╝ ╚═════╝ ╚══════╝"
echo -e "${NC}"
echo -e "${BOLD}  GitHub Push Script for Termux${NC}"
echo "  ========================================"
echo ""

# ── Step 0: Check dependencies ────────────────────
echo -e "${BLUE}[1/7]${NC} Checking dependencies..."

for cmd in git curl; do
  if ! command -v "$cmd" &>/dev/null; then
    echo -e "${RED}✗ '$cmd' not found. Run: pkg install $cmd${NC}"
    exit 1
  fi
done
echo -e "${GREEN}✓ git and curl available${NC}"

# ── Step 1: Gradle Wrapper JAR ────────────────────
echo ""
echo -e "${BLUE}[2/7]${NC} Checking Gradle wrapper JAR..."
JAR="gradle/wrapper/gradle-wrapper.jar"
if [ ! -f "$JAR" ]; then
  echo -e "${YELLOW}  Downloading gradle-wrapper.jar...${NC}"
  mkdir -p gradle/wrapper
  curl -fsSL -o "$JAR" \
    "https://github.com/gradle/gradle/raw/v8.6.0/gradle/wrapper/gradle-wrapper.jar"
  echo -e "${GREEN}✓ gradle-wrapper.jar downloaded${NC}"
else
  echo -e "${GREEN}✓ gradle-wrapper.jar already exists${NC}"
fi
chmod +x gradlew

# ── Step 2: Git config ────────────────────────────
echo ""
echo -e "${BLUE}[3/7]${NC} Checking git config..."

GIT_USER=$(git config --global user.name 2>/dev/null || echo "")
GIT_EMAIL=$(git config --global user.email 2>/dev/null || echo "")

if [ -z "$GIT_USER" ]; then
  echo -e "${YELLOW}  Enter your GitHub username:${NC}"
  read -r GIT_USER
  git config --global user.name "$GIT_USER"
fi

if [ -z "$GIT_EMAIL" ]; then
  echo -e "${YELLOW}  Enter your GitHub email:${NC}"
  read -r GIT_EMAIL
  git config --global user.email "$GIT_EMAIL"
fi

echo -e "${GREEN}✓ Git configured as: $GIT_USER <$GIT_EMAIL>${NC}"

# ── Step 3: GitHub repo ───────────────────────────
echo ""
echo -e "${BLUE}[4/7]${NC} GitHub repository setup..."

if [ -z "$1" ]; then
  echo -e "${YELLOW}  Enter your GitHub repo URL${NC}"
  echo -e "${YELLOW}  (e.g. https://github.com/username/CodeForge):${NC}"
  read -r REPO_URL
else
  REPO_URL="$1"
fi

REPO_URL="${REPO_URL%.git}.git"
echo -e "${GREEN}✓ Repo URL: $REPO_URL${NC}"

# ── Step 4: Git init ──────────────────────────────
echo ""
echo -e "${BLUE}[5/7]${NC} Initializing git repository..."

if [ ! -d ".git" ]; then
  git init
  git checkout -b main 2>/dev/null || git branch -M main
  echo -e "${GREEN}✓ Git initialized${NC}"
else
  echo -e "${GREEN}✓ Git already initialized${NC}"
fi

# Set remote
if git remote get-url origin &>/dev/null; then
  git remote set-url origin "$REPO_URL"
  echo -e "${GREEN}✓ Remote 'origin' updated${NC}"
else
  git remote add origin "$REPO_URL"
  echo -e "${GREEN}✓ Remote 'origin' added${NC}"
fi

# ── Step 5: Stage & Commit ────────────────────────
echo ""
echo -e "${BLUE}[6/7]${NC} Staging all files..."

git add -A

# Count staged files
STAGED=$(git diff --cached --name-only | wc -l)
echo -e "${GREEN}✓ $STAGED files staged${NC}"

echo -e "${YELLOW}  Enter commit message (or press Enter for default):${NC}"
read -r COMMIT_MSG
COMMIT_MSG="${COMMIT_MSG:-"Initial commit: CodeForge APK Builder"}"

git commit -m "$COMMIT_MSG" || {
  echo -e "${YELLOW}  Nothing new to commit. Proceeding...${NC}"
}

# ── Step 6: Push ──────────────────────────────────
echo ""
echo -e "${BLUE}[7/7]${NC} Pushing to GitHub..."
echo -e "${YELLOW}  You may be asked for your GitHub token as password.${NC}"
echo -e "${YELLOW}  (Use a Personal Access Token, not your password)${NC}"
echo ""

if git push -u origin main --force; then
  echo ""
  echo -e "${GREEN}${BOLD}╔══════════════════════════════════════╗${NC}"
  echo -e "${GREEN}${BOLD}║  ✅ PUSH SUCCESSFUL!                  ║${NC}"
  echo -e "${GREEN}${BOLD}╚══════════════════════════════════════╝${NC}"
  echo ""
  echo -e "${CYAN}GitHub Actions will now build your APK!${NC}"
  echo ""
  REPO_WEB="${REPO_URL%.git}"
  REPO_WEB="${REPO_WEB/https:\/\/github.com\//https:\/\/github.com\/}"
  echo -e "  Monitor build: ${BOLD}${REPO_WEB}/actions${NC}"
  echo -e "  Download APK:  ${BOLD}${REPO_WEB}/releases${NC}"
  echo ""
  echo -e "${YELLOW}Build usually takes 3-8 minutes ⏳${NC}"
else
  echo ""
  echo -e "${RED}${BOLD}✗ Push failed!${NC}"
  echo ""
  echo -e "Possible fixes:"
  echo -e "  1. Use Personal Access Token as password"
  echo -e "  2. Check repo URL is correct"
  echo -e "  3. Make sure repo exists on GitHub"
  echo -e "  4. Try: git push -u origin main --force"
  exit 1
fi
