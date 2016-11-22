# Add project specific ProGuard rules here.

# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keep,includedescriptorclasses class com.chrisprime.primestationonecontrolapp.** { *; }
-keep interface com.chrisprime.primestationonecontrolapp.** { *; }

## Android-specific tweaks for this project:
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.app.Activity
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
    void set*(***);
    *** get*();
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep,includedescriptorclasses public class * extends android.support.v4.app.Fragment
-keep,includedescriptorclasses public class * extends android.app.Fragment
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Keep the support libraries
-keep class android.support.** { *; }
-keep interface android.support.** { *; }

# Material progressbar warnings that were causing build failures
-keep,includedescriptorclasses class me.zhanghai.android.materialprogressbar.** { *; }
-dontwarn me.zhanghai.android.materialprogressbar.**

-printconfiguration "../build/proguard-configuration.txt"
-printseeds "../build/proguard-kept-classes-and-members.txt"
-printusage "../build/proguard-stripped-classes-and-members.txt"

#### Timber / log & log string formatting stripping optimizations ####
#-assumenosideeffects public class timber.log.Timber {
#    public static int v(...);
#    public static int i(...);
#    public static int d(...);
#}
    #We only want to keep the warning and error log calls for production builds (strip the rest out to prevent the app from even attempting to format the log strings for these calls)
#    public static int w(...);
#    public static int e(...);

##### 3rd Party Library Log Stripping ####
-assumenosideeffects public class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int d(...);
    public static int w(...);
    public static int e(...);
    public static int wtf(...);
}

#### Primestation-control-android ####
-keep,includedescriptorclasses class com.chrisprime.primestationonecontrol.events.** { *; }

#### Stripping specific library classes that we don't particularly like ####
-assumenosideeffects class com.squareup.leakcanary.** { *; }
-dontnote com.squareup.leakcanary.**

