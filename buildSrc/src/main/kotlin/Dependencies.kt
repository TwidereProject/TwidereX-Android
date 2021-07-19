
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.repositories

fun Project.configRepository() {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}

val Project.enableGoogleVariant: Boolean
    get() = file("google-services.json").exists()

object Lang {
    const val jvmTarget = "11"
    val java = JavaVersion.VERSION_11
}

object Versions {
    const val kotlin = "1.5.10"

    object Kotlin {
        const val lang = "1.5.10"
        const val coroutines = "1.5.0"
        const val serialization = "1.2.1"
    }

    const val ksp = "1.5.10-1.0.0-beta02"
    const val agp = "7.0.0-beta05"
    const val spotless = "5.12.5"
    const val ktlint = "0.41.0"
    const val hilt = "2.37"
    const val okhttp = "4.9.1"
    const val retrofit2 = "2.9.0"
    const val hson = "0.1.4"
    const val compose = "1.0.0-rc02"
    const val constraintLayout = "1.0.0-alpha07"
    const val paging = "3.1.0-alpha02"
    const val paging_compose = "1.0.0-alpha10"
    const val activity = "1.3.0-rc02"
    const val datastore = "1.0.0-rc01"
    const val androidx_hilt = "1.0.0"
    const val room = "2.4.0-alpha03"
    const val lifecycle = "2.4.0-alpha02"
    const val lifecycle_compose = "1.0.0-alpha07"
    const val work = "2.7.0-alpha04"
    const val placeholder = "0.7.0"
    const val zoomable = "1.0.1"
    const val swiper = "0.6.0"
    const val nestedScrollView = "0.7.0"
    const val startup = "1.1.0-alpha01"
    const val coil = "1.3.0"
    const val accompanist = "0.14.0"
    const val androidx_exifinterface = "1.3.2"
    const val exoplayer = "2.14.1"
    const val browser = "1.3.0"
    const val protobuf = "3.17.3"
    const val androidx_test = "1.4.0"
    const val extJUnitVersion = "1.1.3-rc01"
    const val espressoVersion = "3.4.0-rc01"
}

fun DependencyHandlerScope.kotlinCoroutines() {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core", Versions.Kotlin.coroutines)
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test", Versions.Kotlin.coroutines)
}

fun DependencyHandlerScope.kotlinSerialization() {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json", Versions.Kotlin.serialization)
}

fun DependencyHandlerScope.retrofit() {
    api("com.squareup.retrofit2:retrofit", Versions.retrofit2)
    implementation("com.squareup.retrofit2:converter-scalars", Versions.retrofit2)
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter", "0.8.0")
}

fun DependencyHandlerScope.okhttp() {
    implementation("com.squareup.okhttp3:okhttp", Versions.okhttp)
    implementation("com.squareup.okhttp3:logging-interceptor", Versions.okhttp)
}

fun DependencyHandlerScope.junit5() {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
}

fun DependencyHandlerScope.junit4() {
    testImplementation("junit:junit:4.13.2")
}

fun DependencyHandlerScope.hson() {
    implementation("com.github.Tlaster:Hson", Versions.hson)
}

fun DependencyHandlerScope.kspApi() {
    implementation("com.google.devtools.ksp:symbol-processing-api", Versions.ksp)
}

fun DependencyHandlerScope.compose() {
    implementation("androidx.compose.ui:ui", Versions.compose)
    implementation("androidx.compose.ui:ui-tooling", Versions.compose)
    androidTestImplementation("androidx.compose.ui:ui-test", Versions.compose)
    implementation("androidx.compose.foundation:foundation", Versions.compose)
    implementation("androidx.compose.animation:animation", Versions.compose)
    implementation("androidx.compose.material:material", Versions.compose)
    implementation("androidx.compose.material:material-icons-core", Versions.compose)
    implementation("androidx.compose.material:material-icons-extended", Versions.compose)
    implementation("androidx.compose.runtime:runtime-livedata", Versions.compose)
    implementation("androidx.constraintlayout:constraintlayout-compose", Versions.constraintLayout)
}

fun DependencyHandlerScope.paging() {
    implementation("androidx.paging:paging-common", Versions.paging)
    // implementation("androidx.paging:paging-compose", Versions.paging_compose)
}

fun DependencyHandlerScope.activity() {
    implementation("androidx.activity:activity-ktx", Versions.activity)
    implementation("androidx.activity:activity-compose", Versions.activity)
}

fun DependencyHandlerScope.datastore() {
    implementation("androidx.datastore:datastore", Versions.datastore)
    implementation("androidx.datastore:datastore-preferences", Versions.datastore)
}

fun DependencyHandlerScope.hilt() {
    implementation("com.google.dagger:hilt-android", Versions.hilt)
    kapt("com.google.dagger:hilt-android-compiler", Versions.hilt)
    implementation("androidx.hilt:hilt-work", Versions.androidx_hilt)
    kapt("androidx.hilt:hilt-compiler", Versions.androidx_hilt)
}

fun DependencyHandlerScope.room() {
    implementation("androidx.room:room-runtime", Versions.room)
    implementation("androidx.room:room-ktx", Versions.room)
    kapt("androidx.room:room-compiler", Versions.room)
    androidTestImplementation("androidx.room:room-testing", Versions.room)
}

fun DependencyHandlerScope.lifecycle() {
    implementation("androidx.lifecycle:lifecycle-runtime-ktx", Versions.lifecycle)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx", Versions.lifecycle)
    implementation("androidx.lifecycle:lifecycle-livedata-ktx", Versions.lifecycle)
    // implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate", Versions.lifecycle)
    implementation("androidx.lifecycle:lifecycle-common-java8", Versions.lifecycle)
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose", Versions.lifecycle_compose)
}

fun DependencyHandlerScope.android() {
    work()
    room()
    lifecycle()
    activity()
    implementation("androidx.startup:startup-runtime", Versions.startup)
    implementation("io.coil-kt:coil-compose", Versions.coil)
    implementation("androidx.vectordrawable:vectordrawable:1.1.0")
    implementation("androidx.exifinterface:exifinterface", Versions.androidx_exifinterface)
    implementation("com.google.android.exoplayer:exoplayer", Versions.exoplayer)
    implementation("androidx.browser:browser", Versions.browser)
    implementation("com.squareup.leakcanary:leakcanary-android:2.7")
}

fun DependencyHandlerScope.accompanist() {
    implementation("com.google.accompanist:accompanist-insets", Versions.accompanist)
    implementation("com.google.accompanist:accompanist-pager", Versions.accompanist)
    implementation("com.google.accompanist:accompanist-pager-indicators", Versions.accompanist)
}

fun DependencyHandlerScope.work() {
    implementation("androidx.work:work-runtime-ktx", Versions.work)
}

fun DependencyHandlerScope.widget() {
    implementation("com.mxalbert.zoomable:zoomable", Versions.zoomable)
    implementation("com.github.Tlaster:NestedScrollView", Versions.nestedScrollView)
    implementation("com.github.Tlaster:Swiper", Versions.swiper)
    implementation("com.github.Tlaster:Placeholder", Versions.placeholder)
}

fun DependencyHandlerScope.misc() {
    implementation("com.twitter.twittertext:twitter-text:3.1.0")
    implementation("org.jsoup:jsoup:1.13.1")
    implementation("com.google.protobuf:protobuf-javalite", Versions.protobuf)
}

fun DependencyHandlerScope.mockito() {
    testImplementation("org.mockito:mockito-core:3.11.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
}

fun DependencyHandlerScope.androidTest() {
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    androidTestImplementation("androidx.test:core", Versions.androidx_test)
    androidTestImplementation("androidx.test:runner", Versions.androidx_test)
    androidTestImplementation("androidx.test.ext:junit", Versions.extJUnitVersion)
    androidTestImplementation("androidx.test.espresso:espresso-core", Versions.espressoVersion)
}
