# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/ntille/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-verbose
-forceprocessing
-target 7   #We are targeting Java version 1.7 bytecode
-optimizationpasses 5   #Recommended at least 5 optimization passes

#### Primestation-control-android ####
-keep,includedescriptorclasses class com.chrisprime.primestationonecontrol.events.** { *; }

#### Android ####
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep class android.support.** { *; }
-keep interface android.support.** { *; }
-keep public class * extends android.app.Activity
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}
-keepclassmembers class **.R$* {
    public static <fields>;
}
-keep,includedescriptorclasses public class * extends android.support.v4.app.Fragment
-keep,includedescriptorclasses public class * extends android.app.Fragment
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepattributes InnerClasses
-keepattributes EnclosingMethod

#### LeakCanary ####
#
#### Serializables ####
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#### RetroLambda ######
-dontwarn java.lang.invoke.*

#### RxJava ####
-keep,includedescriptorclasses class rx.internal.operators.BufferUntilSubscriber$State { *; }
-keep,includedescriptorclasses class rx.internal.operators.CachedObservable$CachedSubscribe { *; }
-keep,includedescriptorclasses class rx.internal.operators.CachedObservable$ReplayProducer { *; }
-keep,includedescriptorclasses class rx.internal.operators.OnSubscribeFromIterable$IterableProducer { *; }
-keep,includedescriptorclasses class rx.internal.operators.OnSubscribeRange$RangeProducer { *; }
-keep,includedescriptorclasses class rx.internal.operators.OnSubscribeUsing$DisposeAction { *; }
-keep,includedescriptorclasses class rx.internal.operators.OperatorEagerConcatMap$EagerOuterProducer { *; }
-keep,includedescriptorclasses class rx.internal.operators.OperatorMerge$MergeProducer { *; }
-keep,includedescriptorclasses class rx.internal.operators.OperatorPublish$InnerProducer { *; }
-keep,includedescriptorclasses class rx.internal.operators.OperatorReplay$InnerProducer { *; }
-keep,includedescriptorclasses class rx.internal.operators.OperatorReplay$SizeAndTimeBoundReplayBuffer { *; }
-keep,includedescriptorclasses class rx.internal.operators.OperatorZip$Zip { *; }
-keep,includedescriptorclasses class rx.internal.operators.TakeLastQueueProducer { *; }
-keep,includedescriptorclasses class rx.internal.schedulers.ScheduledAction { *; }
-keep,includedescriptorclasses class rx.internal.schedulers.ScheduledAction$Remover { *; }
-keep,includedescriptorclasses class rx.internal.util.BackpressureDrainManager { *; }
-keep,includedescriptorclasses class rx.observables.SyncOnSubscribe$SubscriptionProducer { *; }
-keep,includedescriptorclasses class rx.subjects.ReplaySubject$UnboundedReplayState { *; }
-keep,includedescriptorclasses class rx.subjects.SubjectSubscriptionManager { *; }
-keep,includedescriptorclasses class rx.subscriptions.RefCountSubscription$InnerSubscription { *; }
-keep,includedescriptorclasses class rx.internal.operators.CompletableOnSubscribeConcatArray$ConcatInnerSubscriber { *; }
-keep,includedescriptorclasses class rx.internal.operators.CompletableOnSubscribeConcatIterable$ConcatInnerSubscriber { *; }
-keep,includedescriptorclasses class rx.internal.operators.OperatorBufferWithSize$BufferOverlap$BufferOverlapProducer { *; }
-keep,includedescriptorclasses class rx.internal.operators.OperatorBufferWithSize$BufferSkip$BufferSkipProducer { *; }
-keep,includedescriptorclasses class rx.internal.operators.OperatorGroupBy$State { *; }
-keep,includedescriptorclasses class rx.internal.operators.OperatorWindowWithSize$WindowOverlap$WindowOverlapProducer { *; }
-keep,includedescriptorclasses class rx.internal.operators.OperatorWindowWithSize$WindowSkip$WindowSkipProducer { *; }
-keep,includedescriptorclasses class rx.internal.util.ScalarSynchronousObservable$ScalarAsyncProducer { *; }
-keep,includedescriptorclasses class rx.Producer { *; }
####### https://gist.github.com/kosiara/487868792fbd3214f9c9 #######
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}

#### Butterknife ######
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#### jsch ####
-keep,includedescriptorclasses class com.jcraft.jsch.jce.** { *; }
-keep,includedescriptorclasses class * extends com.jcraft.jsch.KeyExchange { *; }
-keep,includedescriptorclasses class com.jcraft.jsch.** { *; }
#-keep,includedescriptorclasses class com.jcraft.jzlib.ZStream { *; }
-keep,includedescriptorclasses class com.jcraft.jzlib.** { *; }
-keep,includedescriptorclasses class com.jcraft.jsch.Compression { *; }
-keep,includedescriptorclasses class org.ietf.jgss.** { *; }
-dontwarn org.ietf.jgss.**
-dontwarn com.jcraft.jzlib.**


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
-keep,includedescriptorclasses class sun.misc.Unsafe { *; }
-dontnote sun.misc.Unsafe
-keepattributes Signature


#### Otto Event Bus #####
-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

#### Parceler library #####
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keep class org.parceler.Parceler$$Parcels

#### MaterialDialogs ####
-keep,includedescriptorclasses class com.afollestad.materialdialogs.** { *; }

#### Velodrome ####
#-keep,includedescriptorclasses class com.levelmoney.velodrome.Velodrome$ArgGetter  { *; }
#-keepclassmembers class * { @com.levelmoney.velodrome.annotations.OnActivityResult *; }

## Ignoring duplicate classes from subdependencies (there should be a way to actually remove the duplicates, but this is effective enough for our purposes):
-dontnote android.net.http.SslError
-dontnote android.net.http.SslCertificate
-dontnote android.net.http.SslCertificate$DName
-dontnote org.apache.http.conn.scheme.HostNameResolver
-dontnote org.apache.http.conn.scheme.SocketFactory
-dontnote org.apache.http.conn.ConnectTimeoutException
-dontnote org.apache.http.params.HttpParams

## Resolves some standard confusion caused by ProGuard on most projects
-dontwarn android.support.**
-dontnote android.support.**
-dontnote com.google.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService

#### Crypto ####
#-dontnote org.apache.harmony.xnet.provider.jsse.NativeCrypto

#### Google Play Services ####
####### https://developers.google.com/android/guides/setup#Proguard #######
#-keep class * extends java.util.ListResourceBundle {
#    protected java.lang.Object[][] getContents();
#}
#-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
#    public static final *** NULL;
#}
#-keepnames @com.google.android.gms.common.annotation.KeepName class *
#-keepclassmembernames class * {
#    @com.google.android.gms.common.annotation.KeepName *;
#}
#-keepnames class * implements android.os.Parcelable {
#    public static final ** CREATOR;
#}
#-keep,includedescriptorclasses class com.google.android.gms.flags.impl.FlagProviderImpl { *; }

#### Picasso ####
-dontwarn com.squareup.okhttp.**    #OkHttp is now specifically excluded from the Picasso dependency
-dontnote com.squareup.okhttp.OkHttpClient

#### Timber / log & log string formatting stripping optimizations ####
-assumenosideeffects public class timber.log.Timber {
    public static int v(...);
    public static int i(...);
    public static int d(...);
}
    #We only want to keep the warning and error log calls for production builds (strip the rest out to prevent the app from even attempting to format the log strings for these calls)
#    public static int w(...);
#    public static int e(...);

#### 3rd Party Library Log Stripping ####
-assumenosideeffects public class android.util.Log {
    public static int v(...);
    public static int i(...);
    public static int d(...);
    public static int w(...);
    public static int e(...);
}


#### Stripping specific library classes that we don't particularly like ####
#-assumenosideeffects class com.squareup.leakcanary.** { *; }
#-dontnote com.squareup.leakcanary.**
