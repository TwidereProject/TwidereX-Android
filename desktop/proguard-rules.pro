# proguard-rules.pro
-dontoptimize
-dontobfuscate
-ignorewarnings

-dontwarn kotlinx.**

-keepclasseswithmembers public class com.twidere.twiderex.MainKt {
    public static void main(java.lang.String[]);
}
-keep class org.jetbrains.skia.** { *; }
-keep class org.jetbrains.skiko.** { *; }
