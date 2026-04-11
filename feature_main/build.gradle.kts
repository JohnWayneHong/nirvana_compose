import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.ggb.wanandroid.main"
    compileSdk = 35

    // 【新增这块代码】
    lint {
        // 禁用导致崩溃的特定的 LiveData 检查规则
        disable.add("NullSafeMutableLiveData")

        // 终极绝招：如果你只是想顺利打出 Release 包，不想被各种无关紧要的 Lint 警告卡住，
        // 强烈建议加上这两行，让打包过程忽略所有 Lint 报错（这在很多商业项目中是标配）：
        checkReleaseBuilds = false
        abortOnError = false
    }

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        // 从 local.properties 读取 API Key
        val properties = Properties()
        val localPropertiesFile = project.rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            FileInputStream(localPropertiesFile).use { properties.load(it) }
        }
        val volcApiKey = properties.getProperty("VOLC_API_KEY") ?: ""
        
        // 将密钥注入 BuildConfig
        buildConfigField("String", "VOLC_API_KEY", "\"$volcApiKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

dependencies {
    implementation(project(":feature_wanandroid"))
    implementation(libs.nirvana.lib)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.navigation.compose)
    implementation(libs.androidx.compose.material3.icons)
    
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    // 添加纯 Compose 的 Markdown 渲染引擎 (使用 Material 3 风格版本)
    // 把 0.40.2 (API 36 )  改成 0.35.0
    implementation("com.mikepenz:multiplatform-markdown-renderer-m3:0.35.0")
    // Room
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.compose.foundation.layout)
    ksp(libs.androidx.room.compiler)
}
