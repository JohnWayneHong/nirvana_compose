package com.ggb.wanandroidcompose

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ggb.wanandroid.feature.setting.SettingViewModel
import com.ggb.wanandroid.feature.setting.SettingViewModelFactory
import com.ggb.wanandroid.ui.components.LandScapeScreen
import com.ggb.wanandroid.ui.components.LanguageProvider
import com.ggb.wanandroid.ui.components.PortraitScreen
import com.ggb.wanandroid.ui.theme.WanAndroidComposeTheme

class MainActivity : ComponentActivity(){
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 检查是否需要应用保存的语言（第一次启动时）
        checkAndApplyLanguage()
        
        setContent {
            MainScreen()
        }
    }

    private fun checkAndApplyLanguage() {
        // 在 Activity 创建后，检查保存的语言并应用
        // 如果语言和当前不一致，会在 LanguageProvider 中处理
        // 这里主要是确保 Configuration 已更新
    }

    @Composable
    private fun MainScreen() {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp
        val direction = configuration.orientation

        val context = LocalContext.current

        val settingViewModel: SettingViewModel = viewModel(
            factory = SettingViewModelFactory(context)
        )
        val currentLanguage by settingViewModel.curLanguage.collectAsState()

        val theme by settingViewModel.curTheme.collectAsState()
        LanguageProvider(language = currentLanguage) {
            WanAndroidComposeTheme(
                theme = theme
            ){

                if (screenWidth < 600 || direction == Configuration.ORIENTATION_PORTRAIT){
                    PortraitScreen(settingViewModel)
                }else{
                    LandScapeScreen(settingViewModel)
                }
            }
        }
    }

}