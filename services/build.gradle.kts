@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
}

java {
    sourceCompatibility = Versions.Java.java
    targetCompatibility = Versions.Java.java
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(libs.bundles.kotlinx)
    implementation(libs.bundles.reftrofit2)
    implementation(libs.square.okhttp)
    implementation("com.github.Tlaster:Hson:0.1.4")
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.1")
}
