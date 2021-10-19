package com.twidere.twiderex.kmp

enum class Platform {
    Android,
    JVM,
}

expect val currentPlatform: Platform