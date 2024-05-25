# ProGuard rules for Ktor
-keep class io.ktor.** { *; }
-keep class kotlinx.serialization.** { *; }

# ProGuard rules for Coil
-keep class coil.** { *; }
-keep class coil.decode.** { *; }

# ProGuard rules for Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ProGuard rules for AndroidX libraries
-keep class androidx.** { *; }
-dontwarn androidx.**

-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean
-dontwarn org.slf4j.impl.StaticLoggerBinder
