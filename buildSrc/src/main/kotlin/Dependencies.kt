import org.gradle.api.Project
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.repositories

fun Project.configRepository() {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://www.jetbrains.com/intellij-repository/releases")
        maven("https://cache-redirector.jetbrains.com/intellij-dependencies")
    }
}

val Project.enableGoogleVariant: Boolean
    get() = file("google-services.json").exists()

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
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
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

fun DependencyHandlerScope.androidTest() {
    testImplementation("androidx.arch.core:core-testing:2.1.0")
    androidTestImplementation("androidx.arch.core:core-testing:2.1.0")
    androidTestImplementation("androidx.test:core", Versions.androidx_test)
    androidTestImplementation("androidx.test:runner", Versions.androidx_test)
    androidTestImplementation("androidx.test.ext:junit", Versions.extJUnitVersion)
    androidTestImplementation("androidx.test.espresso:espresso-core", Versions.espressoVersion)
}
