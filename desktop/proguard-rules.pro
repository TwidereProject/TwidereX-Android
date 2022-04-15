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

-keep class org.sqlite.** { *; }

-keep class kotlinx.coroutines.swing.** { *; }

-keep class androidx.datastore.core.** { *; }

-keep class kotlin.io.CloseableKt { *; }

-keep class kotlin.reflect.jvm.internal.impl.load.java.** { *; }

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt # core serialization annotations

# kotlinx-serialization-json specific. Add this if you have java.lang.NoClassDefFoundError kotlinx.serialization.json.JsonObjectSerializer
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.twidere.services.**$$serializer { *; }
-keepclassmembers class com.twidere.services.** {
    *** Companion;
}
-keepclasseswithmembers class com.twidere.services.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class com.twidere.twiderex.**$$serializer { *; }
-keepclassmembers class com.twidere.twiderex.** {
    *** Companion;
}
-keepclasseswithmembers class com.twidere.twiderex.** {
    kotlinx.serialization.KSerializer serializer(...);
}
