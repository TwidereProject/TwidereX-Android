plugins {
    kotlin("jvm")
    kotlin("plugin.serialization").version(Versions.kotlin)
}

java {
    sourceCompatibility = Lang.java
    targetCompatibility = Lang.java
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    kotlinCoroutines()
    kotlinSerialization()
    hson()
    retrofit()
    okhttp()
    junit5()
}
