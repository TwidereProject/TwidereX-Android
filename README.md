# Twidere X
[![Build Status](https://github.com/TwidereProject/TwidereX-Android/workflows/Android%20CI/badge.svg)](https://github.com/TwidereProject/TwidereX-Android/actions)
[![Crowdin](https://badges.crowdin.net/twidere-x/localized.svg)](https://crowdin.com/project/twidere-x)
[![Version](https://img.shields.io/github/v/release/TwidereProject/TwidereX-Android)](https://github.com/TwidereProject/TwidereX-Android/releases/latest)
[![Issues](https://img.shields.io/github/issues/TwidereProject/TwidereX-Android)](https://github.com/TwidereProject/TwidereX-Android/issues)
[![License](https://img.shields.io/github/license/TwidereProject/TwidereX-Android)](https://github.com/TwidereProject/TwidereX-Android/blob/develop/LICENSE)
![Activity](https://img.shields.io/github/commit-activity/m/TwidereProject/TwidereX-Android)
[![Contributors](https://img.shields.io/github/contributors/TwidereProject/TwidereX-Android)](https://github.com/TwidereProject/TwidereX-Android/graphs/contributors)

[<img src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" alt="Get it on Google Play" height="80">](https://play.google.com/store/apps/details?id=com.twidere.twiderex)
[<img src="https://f-droid.org/badge/get-it-on.png" alt="Get it on F-Droid" height="80">](https://f-droid.org/en/packages/com.twidere.twiderex/)

Next generation of Twidere for Android 5.0+. **Still in early stage.**  

## Features

- Modern Material Design
- Dark mode
- Tweet with photos
- Multiple account support
- Free, open source, NO ads, forever!


## What's Happening

### What's new in 1.3.0 - Jun 2021 Update

- Twitter/Mastodon thread improve, you can send thread by enabling thread mode in compose page.
- Add [Nitter](https://github.com/zedeus/nitter) support for fetching Twitter thread data.
- Add account notification (background tasks only).
- You can view trends in the search tab now.
- Add options to hide fab/tab/bottom when scrolling timeline.
- The read position now is retained after refreshing the timeline.
- Exif metadata will be removed before uploading.
- UI tweaking for status display, now thread status will display properly.
- Fix input cursor color in compose page.
- Fix status detail blank screen for the last item [#102](https://github.com/TwidereProject/TwidereX-Android/pull/102) by [HuixingWong](https://github.com/HuixingWong)
- Upgrade Jetpack Compose to beta09.

### What is being planned for 1.4.0 - Jun 2021 Update
For 1.4.0, we're working on Twitter DM support, this is one of the most important features that we want, you can check out our [milestore](https://github.com/TwidereProject/TwidereX-Android/milestone/2) for detail. Here is a shortlist:

- Twitter DM (Direct Message) support.
- Fido key and password manager support when login.
- Pure dark mode.
- UI/UX tweaking
- Stability

### Roadmap for 2.0 - Jun 2021 Update
For 2.0, we're considering these options, but it is still an early thought and might change over time.

- Desktop (Linux/Windows) support (by [compose-jb](https://github.com/JetBrains/compose-jb)).
- Tablet mode.
- Javascript extension support.
- View-Only mode (aka Anonymous Browse).

## Contributing

This project welcomes contributions of all types. Help spec'ing, design, documentation, finding bugs are ways everyone can help on top of coding features / bug fixes.

**Before you start work on a feature that you would like to contribute**, please read the [Contributor's Guide](CONTRIBUTING.md).

### ⚠ State of code ⚠

Twidere X is still in an early stage and will be periodically re-structuring/refactoring the code to make it easier to comprehend, navigate, build, test, and contribute to, so **DO expect significant changes to code layout on a regular basis**.

## License
```
                       Twidere X

     Copyright (C) 2020-2021 Tlaster <tlaster@outlook.com>

Twidere X is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Twidere X is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Twidere X. If not, see <http://www.gnu.org/licenses/>.
```
