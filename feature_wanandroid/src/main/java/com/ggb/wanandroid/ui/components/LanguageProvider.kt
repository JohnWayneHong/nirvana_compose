package com.ggb.wanandroid.ui.components

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.ggb.wanandroid.feature.setting.AppLanguage
import java.util.Locale

@Composable
fun LanguageProvider(
    language: AppLanguage,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    
    // 使用 remember 跟踪上次的语言
    var lastLanguage by remember { mutableStateOf<AppLanguage?>(null) }

    LaunchedEffect(language) {
        if (lastLanguage != null && lastLanguage != language) {
            val locale = when(language) {
                AppLanguage.FLOW_SYSTEM -> Locale.getDefault()
                else -> language.locale
            }

            // 更新 Configuration
            val resources = context.resources
            val config = resources.configuration
            config.setLocale(locale)
            
            // 关键：对于已经运行的 Activity，仅 updateConfiguration 可能不够
            // 在 Compose 中，我们需要触发重组或重启 Activity
            resources.updateConfiguration(config, resources.displayMetrics)
            
            // 强制重启 Activity 以应用所有资源的更改（包括 Context 之外的资源）
            (context as? Activity)?.recreate()
        }
        lastLanguage = language
    }

    // 通过 CompositionLocalProvider 确保内部 Composable 看到正确的 Locale
    val locale = when(language) {
        AppLanguage.FLOW_SYSTEM -> Locale.getDefault()
        else -> language.locale
    }
    
    configuration.setLocale(locale)
    
    CompositionLocalProvider(
        LocalConfiguration provides configuration
    ) {
        content()
    }
}