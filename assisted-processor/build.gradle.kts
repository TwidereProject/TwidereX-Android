plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(libs.ksp.symbol.processing.api)
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}
