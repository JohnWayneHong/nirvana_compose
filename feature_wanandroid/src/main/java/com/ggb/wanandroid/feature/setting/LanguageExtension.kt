package com.ggb.wanandroid.feature.setting

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ggb.wanandroid.R
import java.util.Locale

enum class AppLanguage(
    val locale: Locale,
    @StringRes val displayNameRes: Int  // 使用资源 ID 而不是字符串
){
    FLOW_SYSTEM(
        locale = Locale(""),  // 使用空字符串，表示跟随系统
        displayNameRes = R.string.settings_language_follow_system
    ),
    CHINESE(
        locale = Locale("zh", "CN"),
        displayNameRes = R.string.settings_language_zh
    ),
    ENGLISH(
        locale = Locale("en", "US"),
        displayNameRes = R.string.settings_language_en
    ),
    JAPAN(
        locale = Locale("ja", "JP"),
        displayNameRes = R.string.settings_language_ja
    ),
    KOREAN(
        locale = Locale("ko", "KR"),
        displayNameRes = R.string.settings_language_ko
    );
    companion object{

        fun fromLocalCode(code : String) : AppLanguage{
            return when(code){

                "zh" -> CHINESE

                "en" -> ENGLISH

                "ja" -> JAPAN

                "ko" -> KOREAN

                else -> { FLOW_SYSTEM }
            }
        }

        fun allLanguages() : List<AppLanguage>{
            return enumValues<AppLanguage>().toList()
        }
    }
}

/**
 * 获取语言的本地化显示名称
 * 在 Composable 中使用此扩展函数来获取本地化的字符串
 */
@Composable
fun AppLanguage.displayName(): String {
    return stringResource(displayNameRes)
}


data class LanguageConfig(
    val language : AppLanguage,
    val local : Locale,
    val languageCode : String,// “zh”  "en" 等
    val languageCountry : String? = null, // "CN" "US" 等 可选
    val displayName : String
)

object LanguageResources{

    val languageResourceMap = mapOf(
        "zh" to "values-zh",
        "en" to "values-en",
        "ja" to "values-ja",
        "ko" to "values-ko"
    )

    fun getResourceDir(language : String) : String{
        return languageResourceMap[language] ?: "values"
    }
}