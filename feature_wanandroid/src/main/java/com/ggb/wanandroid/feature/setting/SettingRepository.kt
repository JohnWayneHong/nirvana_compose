package com.ggb.wanandroid.feature.setting

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ggb.wanandroid.ui.theme.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map


val Context.settingDataStore : DataStore<Preferences> by preferencesDataStore(name = "setting_preferences")


object SettingPreferencesKeys{
    val THEME = stringPreferencesKey("theme")
    val LANGUAGE = stringPreferencesKey("language")
}


class SettingRepository(context: Context) {


    private val dataStore = context.settingDataStore

    val themeFlow : Flow<AppTheme> = dataStore.data
        .map { preferences ->
            val themeName  = preferences[SettingPreferencesKeys.THEME]
            if (themeName == null) {
                AppTheme.AUTO
            } else {
                try {
                    AppTheme.valueOf(themeName)
                } catch (e: IllegalArgumentException) {
                    AppTheme.AUTO
                }
            }
        }


    suspend fun saveTheme(theme : AppTheme){
        dataStore.edit { preferences ->
            // 保存枚举名称（如 "DARK", "LIGHT"），而不是 themeName（如 "深色主题"）
            // 因为 AppTheme.valueOf() 需要枚举名称
            preferences[SettingPreferencesKeys.THEME] = theme.name
        }
    }


    val languageFlow : Flow<AppLanguage> = dataStore.data
        .map { preferences ->
            val languageCode = preferences[SettingPreferencesKeys.LANGUAGE]
            if (languageCode == null || languageCode.isEmpty()) {
                AppLanguage.FLOW_SYSTEM
            } else {
                AppLanguage.fromLocalCode(languageCode)
            }
        }


    suspend fun saveLanguage(language : AppLanguage){
        dataStore.edit { preferences ->
            if (language == AppLanguage.FLOW_SYSTEM) {
                preferences.remove(SettingPreferencesKeys.LANGUAGE)
            } else {
                // 关键修复：对于繁体中文，language.locale.language 只返回 "zh"
                // 必须保存完整的标识，例如 "zh-TW" 或 "zh-rTW"
                val code = if (language == AppLanguage.TRADITIONAL_CHINESE) "zh-TW" else language.locale.language
                preferences[SettingPreferencesKeys.LANGUAGE] = code
            }
        }
    }
}