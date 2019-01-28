# Release

1. Update `meta.versionName` in `gradle/meta.properties`.
1. Update version in `README.md`.
1. Commit changes.
1. `./gradlew clean build uploadArchives -Prelease`
1. Check if the repository containing the archive can be closed at oss.sonatype.org.
1. `./gradlew publishPlugin` to plugins.gradle.org.
1. Release modules manually at oss.sonatype.org.
