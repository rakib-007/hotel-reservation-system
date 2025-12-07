# How to Push to GitHub

## Step 1: Create a GitHub Repository

1. Go to https://github.com
2. Click the "+" icon in the top right → "New repository"
3. Name it: `hotel-reservation-system` (or any name you prefer)
4. **DO NOT** initialize with README, .gitignore, or license (we already have these)
5. Click "Create repository"

## Step 2: Add Remote and Push

After creating the repository, GitHub will show you commands. Use these:

```bash
cd "/Volumes/Rakib/Hotel Reservation System/Hotel Reservation System/hotel-reservation"

# Add your GitHub repository as remote (replace YOUR_USERNAME with your GitHub username)
git remote add origin https://github.com/YOUR_USERNAME/hotel-reservation-system.git

# Or if you prefer SSH:
# git remote add origin git@github.com:YOUR_USERNAME/hotel-reservation-system.git

# Push to GitHub
git branch -M main
git push -u origin main
```

## Alternative: If you already have a repository URL

If you already created the repository, just replace `YOUR_USERNAME` and `hotel-reservation-system` with your actual values:

```bash
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO_NAME.git
git branch -M main
git push -u origin main
```

## What's Included

✅ All source code (17 Java files)
✅ FXML UI files
✅ Database schema
✅ Maven configuration (pom.xml)
✅ Documentation (README.md)
✅ Windows batch file for easy running
✅ .gitignore (excludes build files, logs, database files)

## What's Excluded (via .gitignore)

❌ `target/` folder (build artifacts)
❌ `database/hotel.db` (database file - will be created on first run)
❌ Log files
❌ IDE configuration files

