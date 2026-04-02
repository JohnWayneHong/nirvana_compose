package com.ggb.wanandroid.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.ggb.wanandroid.feature.setting.AppLanguage
import java.util.Locale

@Composable
fun LanguageProvider(
    language: AppLanguage,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    
    // 使用 remember 跟踪上次的语言，避免重复更新
    var lastLanguage by remember { mutableStateOf<AppLanguage?>(null) }

    LaunchedEffect(language) {
        // 只在语言真正变化时才更新
        if (lastLanguage != language) {
            val locale = when(language) {
                AppLanguage.FLOW_SYSTEM -> Locale.getDefault()
                else -> language.locale
            }

            // 更新 Configuration
            val config = context.resources.configuration
            config.setLocale(locale)
            context.resources.updateConfiguration(config, context.resources.displayMetrics)
            
            lastLanguage = language
            
            // 注意：不在这里重建 Activity，避免无限循环
            // 字符串资源的更新会在下次重组时自动应用
            // 如果需要立即看到效果，可以在用户选择语言后手动关闭对话框并刷新页面
        }
    }

    content()
}