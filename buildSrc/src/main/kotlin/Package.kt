object Package {
    const val group = "com.twidere"
    const val name = "Twidere X"
    const val id = "$group.twiderex"
    val versionName =
        "${Version.main}.${Version.mirror}.${Version.patch}${if (Version.revision.isNotEmpty()) "-${Version.revision}" else ""}"
    const val copyright = "Copyright (C) TwidereProject and Contributors"
    const val versionCode = Version.build

    object Version {
        const val main = "1"
        const val mirror = "6"
        const val patch = "0"
        const val revision = "dev02"
        const val build = 57
    }
}
