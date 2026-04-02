package com.ggb.wanandroidcompose.global

import android.app.Application
import android.content.Context
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.crossfade
import com.zfx.commonlib.network.config.NetworkConfigBuilder
import com.zfx.commonlib.network.extension.initNetworkManager
import com.zfx.commonlib.network.interceptor.LoggingInterceptor
import com.zfx.commonlib.network.interceptor.LoginInterceptor
import com.zfx.commonlib.network.repository.BaseRepository
import com.zfx.commonlib.util.StringResourceHelper
import com.ggb.wanandroid.feature.setting.AppLanguage
import com.ggb.wanandroid.feature.setting.SettingPreferencesKeys
import com.ggb.wanandroid.feature.setting.settingDataStore
import com.ggb.wanandroid.navigation.Routes
import com.ggb.wanandroid.util.AddCookieInterceptor
import com.ggb.wanandroid.util.CookieInterceptor
import com.ggb.wanandroid.util.NavControllerManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Locale

class App : Application(), SingletonImageLoader.Factory {

    override fun attachBaseContext(base: Context) {
        // 在 attachBaseContext 中，Application 还未完全初始化，不能使用 DataStore
        // 先使用默认语言，在 onCreate 中会重新设置
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        
        // 在 onCreate 中读取并应用保存的语言
        applySavedLanguage()

        StringResourceHelper.init(this)
        initNetworkManager(
            NetworkConfigBuilder()
                .baseUrl("https://www.wanandroid.com")
                .addInterceptor(AddCookieInterceptor())  // 请求时携带本地 Cookie
                .addInterceptor(CookieInterceptor())   // 响应时保存 Set-Cookie
                .logLevel(LoggingInterceptor.LogLevel.BODY)
                .build()
        )

        BaseRepository.setLoginInterceptor(object : LoginInterceptor{
            override fun onUnauthorized(errorCode: Int, errorMessage: String) {
                // 未授权时，跳转到登录页面
                NavControllerManager.navigate(Routes.ACCOUNT)
            }
        }).unauthorizedCodes(setOf(1001))
            .interceptWindowMillis(3000)
    }
    
    private fun applySavedLanguage() {
        // 在 onCreate 中，Application 已初始化，可以使用 DataStore
        runBlocking {
            try {
                val dataStore = this@App.settingDataStore
                val preferences = dataStore.data.first()
                val languageCode = preferences[SettingPreferencesKeys.LANGUAGE]

                
                val savedLanguage = if (languageCode == null || languageCode.isEmpty()) {
                    AppLanguage.FLOW_SYSTEM
                } else {
                    val language = AppLanguage.fromLocalCode(languageCode)
                    language
                }
                
                val locale = when (savedLanguage) {
                    AppLanguage.FLOW_SYSTEM -> Locale.getDefault()
                    else -> savedLanguage.locale
                }

                
                // 更新 Configuration
                val config = resources.configuration
                config.setLocale(locale)
                resources.updateConfiguration(config, resources.displayMetrics)
                
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .build()
    }
}