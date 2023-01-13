# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# [Issue # 4] 앱 배포 시, 코드 축소, 난독화, 최적화를 하는 경우, 카카오 SDK를 제외하고 진행하기 위하여 추가함
-keep class com.kakao.sdk.**.model.* { <fields>; }
-keep class * extends com.google.gson.TypeAdapter

# 앱 배포시, 코드 축소, 난독화, 최적화를 하는 경우, 구글 SDK를 난독화 제외
-keep class com.google.android.gms.** { *; }

# retrofit 사용시 json을 생성/파싱을 위한 class들에 대해 난독화 제외
-keep class com.depromeet.sloth.data.model.** { *; }

# navigation 사용시 argument 로전달하는 class들에 대해 난독화 제외
-keep class com.depromeet.sloth.presentation.model.** { *; }