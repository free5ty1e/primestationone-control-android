# Proguard config for LoganSquare
# https://github.com/bluelinelabs/LoganSquare#proguard

-keep,includedescriptorclasses class com.bluelinelabs.logansquare.** { *; }
-keep,includedescriptorclasses @com.bluelinelabs.logansquare.annotation.JsonObject class *
-keep,includedescriptorclasses class **$$JsonObjectMapper { *; }
-keep,includedescriptorclasses class com.fasterxml.jackson.core.** { *; }
