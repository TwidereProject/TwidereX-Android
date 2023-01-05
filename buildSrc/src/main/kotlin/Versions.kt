import org.gradle.api.JavaVersion

object Versions {
    object Kotlin {
        const val lang = "1.7.20"
        const val coroutines = "1.6.4"
        const val serialization = "1.4.1"
        const val immutableCollections = "0.3.5"
    }

    object Java {
        const val jvmTarget = "11"
        val java = JavaVersion.VERSION_11
    }

    const val ksp = "${Kotlin.lang}-1.0.8"
    const val agp = "7.3.1"
    const val spotless = "6.12.1"
    const val ktlint = "0.46.1"
    const val okhttp = "4.10.0"
    const val retrofit2 = "2.9.0"
    const val hson = "0.1.4"
    const val compose_jb = "1.3.0-rc01"
    const val paging = "3.2.0-alpha03"
    const val activity = "1.7.0-alpha02"
    const val datastore = "1.1.0-alpha01"
    const val room = "2.5.0-rc01"
    const val lifecycle = "2.6.0-alpha03"
    const val work = "2.8.0-rc01"
    const val startup = "1.2.0-alpha01"
    const val coil = "2.2.2"
    const val accompanist = "0.28.0"
    const val accompanist_jb = "0.25.2"
    const val androidx_exifinterface = "1.3.5"
    const val exoplayer = "2.18.2"
    const val browser = "1.5.0-alpha02"
    const val protobuf = "3.21.12"
    const val androidxTestCore = "1.5.0"
    const val androidxTestRunner = "1.5.1"
    const val extJUnitVersion = "1.1.4"
    const val espressoVersion = "3.5.0"
    const val koin = "3.3.2"
    const val moko = "0.20.1"
    const val sqlDelight = "1.5.4"
    const val javafx = "0.0.13"
    const val kFilePicker = "1.0.4"
    const val jodaTime = "2.12.2"
    const val cache4k  = "0.9.0"
    const val precompose = "1.3.13"
    const val precomposeKsp = "1.0.2"
    const val twitterParser = "0.2.1"
}
