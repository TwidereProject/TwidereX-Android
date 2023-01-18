import org.gradle.api.JavaVersion

object Versions {
    object Android {
      const val min = 21
      const val compile = 33
      const val target = compile
    }

    object Java {
        const val jvmTarget = "11"
        val java = JavaVersion.VERSION_17
    }
}
