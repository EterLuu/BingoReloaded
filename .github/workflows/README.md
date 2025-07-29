# GitHub Actions Workflows

This directory contains GitHub Actions workflows for automated building, testing, and releasing of BingoReloaded.

## Workflows

### 🔨 `build.yml` - Build and Test
**Triggers:** Push to `main`/`develop` branches, Pull requests to `main`

**Purpose:** Continuous integration workflow that builds the project and runs tests on every push and pull request.

**Steps:**
- Sets up JDK 21 (required for the project)
- **Auto-fixes missing Gradle wrapper JAR** (common issue)
- Caches Gradle dependencies for faster builds
- Compiles both BingoReloaded and PlayerDisplay modules
- Runs all tests
- Creates shadow JAR (fat JAR with all dependencies)
- Uploads build artifacts for 30 days
- Uploads test reports for 7 days

### 🚀 `release.yml` - Release Build
**Triggers:** 
- New GitHub release is published
- Manual workflow dispatch with tag input

**Purpose:** Creates production-ready builds for releases.

**Steps:**
- Builds clean release JARs
- Extracts version from build.gradle
- Renames JARs with proper version naming
- Uploads JAR to GitHub release (automatic releases)
- Stores artifacts for 90 days (manual releases)

### ✅ `pr-validation.yml` - Pull Request Validation
**Triggers:** Pull requests opened/updated against `main`/`develop`

**Purpose:** Validates pull requests before merging.

**Steps:**
- Validates Gradle wrapper integrity
- Runs optional code style checks
- Compiles code and tests
- Tests shadow JAR creation
- Checks for build warnings
- Posts build status comment on PR

## Build Artifacts

After successful builds, you can find:

- **Main Plugin JAR:** `BingoReloaded/build/libs/BingoReloaded-{version}.jar`
- **PlayerDisplay JAR:** `PlayerDisplay/build/libs/PlayerDisplay-{version}.jar`

The shadow JAR contains all dependencies and is ready for deployment to a Minecraft server.

## Requirements

- **Java 21** (as specified in build.gradle)
- **Gradle** (wrapper included)
- **Paper API 1.21.7+** (for compilation)

## Usage

1. Push code to trigger automatic builds
2. Create a GitHub release to trigger release builds
3. Open a PR to trigger validation checks
4. Download artifacts from the Actions tab

## Gradle Commands

For local development:
```bash
./gradlew build          # Build both modules
./gradlew test           # Run tests
./gradlew shadowJar      # Create fat JAR
./gradlew clean build    # Clean build
```

## Troubleshooting

### ❌ "Could not find or load main class org.gradle.wrapper.GradleWrapperMain"

This error occurs when `gradle/wrapper/gradle-wrapper.jar` is missing. The GitHub Actions are configured to automatically fix this by downloading the missing JAR file.

**For local development, fix manually:**
```bash
# Download the missing wrapper JAR
curl -L -o gradle/wrapper/gradle-wrapper.jar https://github.com/gradle/gradle/raw/v8.14.0/gradle/wrapper/gradle-wrapper.jar
chmod +x gradlew
```

### ❌ Build fails with "Java 21 required"

Ensure you have Java 21 installed locally:
```bash
java -version  # Should show Java 21
```

The GitHub Actions automatically use Java 21, so this only affects local builds.