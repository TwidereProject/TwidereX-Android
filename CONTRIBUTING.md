# How To Contribute

First of all, I'd like to express my appreciation to you for contributing to this project. 
Below is the guidance for how to report issues, propose new features, and submit contributions via Pull Requests (PRs).

## Before you start, file an issue
If you have a question, think you've discovered an issue, would like to propose a new feature, etc., then find/file an issue **BEFORE** starting work to fix/implement it.

### Search existing issues first

Before filing a new issue, search existing open and closed issues first: It is likely someone else has found the problem you're seeing, and someone may be working on or have already contributed a fix!

If no existing item describes your issue/feature, great - please file a new issue.

## Contributing fixes / features

For those able & willing to help fix issues and/or implement features ...

### Development environment

Make sure you have
 - Android Studio Arctic Fox 2020.3.1 or IntelliJ IDEA 2021.3
 - JDK 17  

### Working branch

You should make changes on the `develop` branch, changes will be merged into `master` branch after a stable version released.

### Code guidelines
Twidere X uses [ktlint](https://github.com/pinterest/ktlint) to check the code style, so make sure run `./gradlew spotlessCheck` and fix the errors before you submit any PR.  
Twidere X treat warnings as errors, **DO NOT** use `@Suppress` to ignore any warnings unless you known what's happening.

#### For UI code
Twidere X follows [Compose Api Guidelines](https://github.com/androidx/androidx/blob/androidx-main/compose/docs/compose-api-guidelines.md), please read the guidelines before you contribute.  