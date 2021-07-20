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

### What's new in 1.4.0 - Jul 2021 Update

- Twitter DM (Direct Message) support is here!
- Now you can use Fido key and password manager when login.
- Add notification setting in the application.
- Add pure dark mode support when dark theme is selected, which is really black.
- Fix nitter usage for viewing private tweets, now you can view private tweet thread when using nitter.
- Fix some really old mention is being notified when using Twitter.
- Fix when the TopBar BottomBar and FloatingButton sometimes in the intermediate state [#152](https://github.com/TwidereProject/TwidereX-Android/pull/152) by [HuixingWong](https://github.com/HuixingWong)
- Fix the status gap algorithm, now the gap is more accurate than before.
- Fix VideoPlayer is still playing when the app is in background [#173](https://github.com/TwidereProject/TwidereX-Android/pull/173) by [HuixingWong](https://github.com/HuixingWong)
- Rework for text input, now the beginning word in sentences is auto-capitalized when compose.
- Fix notification timestamp not being used, the can fix the issue where old notification still being notified.
- Fix crashing when the user cancels adding the member to the list.
- Fix timeline not able to refresh after changing account.
- Fix clicking on notification opens blank app.
- Upgrade Jetpack Compose to RC02.

### What is being planned for 1.5.0 - Jul 2021 Update
For 1.5.0, as we've finished the basic functionality for Twitter and Mastodon, we're now focusing on the functionality that is lacking in Twidere X, such as Tabs editing, proxy support, and UX improvement, you can check out our [milestore](https://github.com/TwidereProject/TwidereX-Android/milestone/3) for detail. Here is a shortlist:

- Proxy support.
- Tabs editing support.
- Internal changes that preparing for the desktop version.
- Bug fixes.
- UI/UX tweaking.
- Stability.

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
