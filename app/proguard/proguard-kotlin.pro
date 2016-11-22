#### Kotlin ####
-dontwarn kotlin.**
#-keep class kotlin.reflect.jvm.internal.ReflectionFactoryImpl { *; }
-dontnote kotlin.reflect.jvm.internal.ReflectionFactoryImpl
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}
-keep,includedescriptorclasses class kotlin.jvm.internal.** { *; }
-keep,includedescriptorclasses class kotlin.jvm.functions.** { *; }
-keep,includedescriptorclasses class kotlin.sequences.** { *; }
-keep,includedescriptorclasses class kotlin.text.** { *; }
