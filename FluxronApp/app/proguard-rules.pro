# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/theowinter/Library/Android/sdk/tools/proguard/proguard-android.txt
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

# Do not obfuscate event handler methods
# as these are bound on runtime by GreenRobot EventBus
-keepclassmembers class ** {
    public void onEvent*(**);
}

# Required for GreenRobot EventBus with AsyncExecutor
-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}