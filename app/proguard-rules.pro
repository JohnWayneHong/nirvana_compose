# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# ============================================
# 基础配置
# ============================================

# 保留行号信息，用于调试堆栈跟踪
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# 保留注解
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# 保留泛型信息
-keepattributes Signature

# ============================================
# Kotlin 相关
# ============================================

# Kotlin 协程
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.** {
    volatile <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# Kotlin 反射
-keep class kotlin.reflect.** { *; }
-keep class kotlin.Metadata { *; }

# Kotlin 序列化
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt

# ============================================
# Jetpack Compose 相关
# ============================================

# Compose Runtime
-keep class androidx.compose.runtime.** { *; }
-keep class androidx.compose.ui.** { *; }
-keep class androidx.compose.foundation.** { *; }
-keep class androidx.compose.material.** { *; }
-keep class androidx.compose.material3.** { *; }
-keep class androidx.compose.animation.** { *; }

# Compose Compiler
-keep class androidx.compose.compiler.** { *; }

# ============================================
# AndroidX 相关
# ============================================

# AndroidX Core
-keep class androidx.core.** { *; }
-keep interface androidx.core.** { *; }

# Lifecycle
-keep class androidx.lifecycle.** { *; }
-keep interface androidx.lifecycle.** { *; }

# Navigation Compose
-keep class androidx.navigation.** { *; }
-keep interface androidx.navigation.** { *; }

# Activity Compose
-keep class androidx.activity.compose.** { *; }

# ============================================
# 网络请求相关 (Retrofit, OkHttp, Gson)
# ============================================

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn javax.annotation.**
-dontwarn kotlin.Unit
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# ============================================
# Coil 图片加载库
# ============================================

-keep class coil.** { *; }
-keep interface coil.** { *; }
-dontwarn coil.**

# ============================================
# 数据类 (Data Class)
# ============================================

# 保留数据类的成员
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <methods>;
}

# ============================================
# 序列化/反序列化
# ============================================

# 保留所有实现 Serializable 接口的类
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ============================================
# 反射相关
# ============================================

# 保留使用反射的类
-keepclassmembers class * {
    @androidx.annotation.Keep <methods>;
}

# ============================================
# WebView
# ============================================

# 如果项目使用 WebView 与 JS 交互，取消注释并指定 JavaScript 接口类的完全限定名
# -keepclassmembers class fqcn.of.javascript.interface.for.webview {
#    public *;
# }

# ============================================
# 第三方库 (根据项目需要添加)
# ============================================

# 如果使用 zfx.lib，保留相关类
-keep class com.zfx.** { *; }
-dontwarn com.zfx.**

# ============================================
# 应用入口
# ============================================

# 保留 Application 类
-keep class com.ggb.wanandroidcompose.** { *; }

# 保留所有 Activity
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends androidx.fragment.app.Fragment

# ============================================
# 优化配置
# ============================================

# 不警告缺失的类
-dontwarn javax.annotation.**
-dontwarn kotlin.**
-dontwarn kotlinx.**
-dontwarn org.jetbrains.annotations.**

# 优化代码
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# 移除日志
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}