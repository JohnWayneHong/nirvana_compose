package com.ggb.wanandroid.feature.setting.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.width
import androidx.navigation.NavController
import com.ggb.wanandroid.R
import com.ggb.wanandroid.feature.setting.SettingViewModel
import com.ggb.wanandroid.feature.setting.displayName
import com.ggb.wanandroid.feature.setting.settingViewModel
import com.ggb.wanandroid.ui.theme.themeName
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun SettingScreen(
    navController: NavController,
    viewModel: SettingViewModel? = null
){
    // 如果传入了 viewModel，使用传入的；否则创建新的
    val settingViewModel = viewModel ?: settingViewModel()
    
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    val coroutineScope = rememberCoroutineScope()

    val theme by settingViewModel.curTheme.collectAsState()
    
    // 控制主题选择对话框的显示
    var showThemeDialog by remember { mutableStateOf(false) }


    // 控制语言选择对话框的显示
    var showLanguageDialog by remember { mutableStateOf(false) }

    val language by settingViewModel.curLanguage.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary
                    ),
                verticalAlignment = Alignment.CenterVertically
            ){
                Spacer(
                    Modifier.size(12.dp)
                )

                Image(
                    modifier = Modifier
                        .height(24.dp)
                        .width(16.dp)
                        .clickable {
                            navController.popBackStack()
                                   },
                    painter = painterResource(R.drawable.icon_back_white),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                    contentDescription = stringResource(R.string.settings_back_button)
                )

                Spacer(
                    Modifier.size(12.dp)
                )

                Text(
                    text = stringResource(R.string.title_settings),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(
                    color = MaterialTheme.colorScheme.onPrimary
                )
        ) {

            SettingItem(
                name = stringResource(R.string.settings_theme),
                setName = theme.themeName(),  // 使用扩展函数获取本地化字符串
                onItemClick = {
                    showThemeDialog = true  // 显示对话框
                }
            )
            
            // 主题选择对话框
            if (showThemeDialog) {
                ThemeSelectDialog(
                    onSelect = { selectedTheme ->
                        settingViewModel.setTheme(selectedTheme)  // 保存选中的主题
                        showThemeDialog = false  // 关闭对话框
                    },
                    onDismiss = {
                        showThemeDialog = false  // 关闭对话框
                    }
                )
            }


            SettingItem(
                name = stringResource(R.string.settings_language),
                setName = language.displayName(),  // 使用扩展函数获取本地化字符串
                onItemClick = {
                    showLanguageDialog = true
                }
            )

            if (showLanguageDialog){
                LanguageSelectDialog(
                    onDismiss = {
                        showLanguageDialog = false
                    },
                    onLanguageSelect = { selectLanguage ->
                        // 关闭对话框
                        showLanguageDialog = false
                        // 保存语言并等待保存完成后再重启
                        coroutineScope.launch {
                            settingViewModel.saveLanguage(selectLanguage)
                            // 等待一小段时间确保 DataStore 保存完成
                            delay(200)
                            // 重启应用
                            activity?.recreate()
                        }
                    },
                    currentLanguage = language
                )
            }

        }

    }


}


@Composable
fun SettingItem(
    name : String,
    setName : String,
    onItemClick : () -> Unit
){
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(40.dp)
            .clickable{
                onItemClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = name,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
            fontSize = 12.sp
        )

        Spacer(Modifier.weight(1f))

        Text(
            text = setName,
            color = if (name == stringResource(R.string.settings_theme)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onTertiary,
            fontSize = 12.sp
        )

        Spacer(Modifier.size(12.dp))

        Image(
            painter = painterResource(R.drawable.icon_arrow_right_gray),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
            contentDescription = stringResource(R.string.settings_arrow_right)
        )

    }
}