import org.gradle.api.JavaVersion

object Versions {
    object Kotlin {
        const val lang = "1.6.10"
        const val coroutines = "1.6.0"
        const val serialization = "1.3.2"
    }

    object Java {
        const val jvmTarget = "17"
        val java = JavaVersion.VERSION_17
    }

    const val ksp = "${Kotlin.lang}-1.0.4"
    const val agp = "7.2.0-rc01"
    const val spotless = "6.4.2"
    const val ktlint = "0.45.2"
    const val okhttp = "4.9.1"
    const val retrofit2 = "2.9.0"
    const val hson = "0.1.4"
    const val compose_jb = "1.1.1"
    const val paging = "3.1.0"
    const val activity = "1.4.0"
    const val datastore = "1.0.0"
    const val androidx_hilt = "1.0.0"
    const val room = "2.4.2"
    const val lifecycle = "2.4.1"
    const val lifecycle_compose = "2.4.1"
    const val work = "2.7.1"
    const val startup = "1.1.0"
    const val coil = "2.0.0-rc03"
    const val accompanist = "0.23.0"
    const val accompanist_jb = "0.20.1"
    const val androidx_exifinterface = "1.3.3"
    const val exoplayer = "2.17.1"
    const val browser = "1.4.0"
    const val protobuf = "3.20.0"
    const val androidx_test = "1.4.1-alpha03"
    const val extJUnitVersion = "1.1.4-alpha03"
    const val espressoVersion = "3.5.0-alpha03"
    const val koin = "3.2.0-beta-1"
    const val moko = "0.19.0"
    const val sqlDelight = "1.5.3"
    const val javafx = "0.0.10"
    const val kFilePicker = "1.0.4"
    const val jodaTime = "2.10.13"
}
