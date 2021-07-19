plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    kspApi()
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}
