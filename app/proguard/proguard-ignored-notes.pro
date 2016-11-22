##################### Ignoring duplicate classes from subdependencies (there should be a way to actually remove the duplicates, but this is effective enough for our purposes):
-dontnote android.net.http.SslError
-dontnote android.net.http.SslCertificate
-dontnote android.net.http.SslCertificate$DName
-dontnote org.apache.http.conn.scheme.HostNameResolver
-dontnote org.apache.http.conn.scheme.SocketFactory
-dontnote org.apache.http.conn.ConnectTimeoutException
-dontnote org.apache.http.params.HttpParams

##################### Ignoring dynamically-referenced classes that can't be found but don't seem to matter:
#### Android ####
-dontnote android.support.**
-dontnote com.google.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService

#### Dagger rules reference some potentially unused classes:
-dontnote dagger.internal.**
#### ...and Dagger rules also refer to this incorrectly, so:
-dontnote Object

#### Picasso ####
-dontwarn com.squareup.okhttp.**    #OkHttp is now specifically excluded from the Picasso dependency
-dontnote com.squareup.okhttp.OkHttpClient

##################### Ignoring missing descriptor classes for specifically-kept classes:
#### Material dialogs ####
-dontnote com.afollestad.materialdialogs.**

#### Pinch-and-zoom view (subsamplingimageview) ####
-dontnote com.davemorrissey.labs.subscaleview.**

#### Okio ####
-dontnote okio.**

#### RxJava ####
-dontnote rx.**
-dontnote **.Unsafe

#### Some duplicate classes since updating to API 25 ####
-dontnote org.apache.http.params.HttpConnectionParams
-dontnote org.apache.http.params.CoreConnectionPNames
-dontnote org.apache.http.conn.scheme.LayeredSocketFactory
-dontnote android.net.http.HttpResponseCache
