#### GSON ####
####### https://github.com/google/gson/blob/master/examples/android-proguard-example/proguard.cfg #######
-keep class com.google.gson.** { *; }
-keep class com.google.inject.** { *; }
-dontwarn com.google.gson.**
# Gson uses generic type information stored in a class file when working with
# fields. Proguard removes such information by default, so configure it to keep
# all of it. Facebook SDK Also requires this.
#-keepattributes Signature  #Duplicate, already defined under Retrofit.  Might need to uncomment this if we strip out Retrofit at a later date.
-keepclassmembers enum * {
     public static **[] values();
     public static ** valueOf(java.lang.String);
}
# For using GSON @Expose annotation
#-keepattributes *Annotation* #Duplicate requirement handled in Retrofit Section
# Gson specific classes
-keepattributes Signature
#Note: com.google.gson.internal.UnsafeAllocator accesses a declared field 'theUnsafe' dynamically
#-keep class com.google.gson.internal.UnsafeAllocator { java.lang.reflect.Field theUnsafe; }
#Resulting Note: the configuration refers to the unknown field 'java.lang.reflect.Field theUnsafe' in class 'com.google.gson.internal.UnsafeAllocator'
-keep,includedescriptorclasses class com.google.gson.internal.UnsafeAllocator { *; }
-dontnote com.google.gson.internal.UnsafeAllocator
-keep class sun.misc.Unsafe { *; }
-dontnote sun.misc.Unsafe
