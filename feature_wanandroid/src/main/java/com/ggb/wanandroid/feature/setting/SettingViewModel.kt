package com.ggb.wanandroid.feature.setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.ggb.wanandroid.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingViewModel(
    private val repository: SettingRepository
) : ViewModel() {

    private var _curTheme = MutableStateFlow<AppTheme>(AppTheme.AUTO)
    val curTheme: StateFlow<AppTheme> = _curTheme.asStateFlow()


    private var _curLanguage = MutableStateFlow<AppLanguage>(AppLanguage.FLOW_SYSTEM)
    val  curLanguage: StateFlow<AppLanguage> = _curLanguage.asStateFlow()

    init {
        // 监听 DataStore 的变化，Flow 会在订阅时立即发出当前值，无需单独 loadTheme()
        // 需要分别启动两个协程来收集两个 Flow，因为它们都是持续发出的
        viewModelScope.launch {
            repository.themeFlow.collect { theme ->
                _curTheme.value = theme
            }
        }
        
        viewModelScope.launch {
            repository.languageFlow.collect { language ->
                // 只在语言真正变化时才更新，避免覆盖手动设置的值
                if (_curLanguage.value != language) {
                    _curLanguage.value = language
                }
            }
        }
    }

    fun saveTheme(theme: AppTheme) {
        viewModelScope.launch {
            repository.saveTheme(theme)
            // 立即更新状态，确保 UI 立即响应
            _curTheme.value = theme
        }
    }
    
    fun setTheme(theme: AppTheme) {
        saveTheme(theme)
    }


    fun saveLanguage(language: AppLanguage) {
        viewModelScope.launch {
            // 保存到 DataStore
            // languageFlow 会自动检测到变化并更新 _curLanguage，无需手动更新
            repository.saveLanguage(language)
        }
    }
}

/**
 * ViewModel Factory，用于创建 SettingViewModel
 */
class SettingViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingViewModel(SettingRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * 在 Composable 中获取 SettingViewModel 的辅助函数
 */
@Composable
fun settingViewModel(): SettingViewModel {
    val context = LocalContext.current
    return viewModel(
        factory = SettingViewModelFactory(context)
    )
}