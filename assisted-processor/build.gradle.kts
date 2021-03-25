plugins {
    kotlin("jvm")
}

group = "com.twidere"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.devtools.ksp:symbol-processing-api:1.4.31-1.0.0-alpha06")
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}
