package com.ggb.wanandroid.feature.setting.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ggb.wanandroid.R
import com.ggb.wanandroid.feature.setting.AppLanguage
import com.ggb.wanandroid.feature.setting.displayName


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectDialog(
    currentLanguage : AppLanguage,
    onLanguageSelect : (AppLanguage) -> Unit,
    onDismiss : () -> Unit = {}
){

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val languages = remember {
        AppLanguage.allLanguages()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.onPrimary,  // 设置对话框背景色
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),  // 只设置上面两个角的圆角
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                modifier = Modifier,
                text = stringResource(R.string.settings_language),
                color = MaterialTheme.colorScheme.surface,  // 使用 primary 颜色，在白色背景上显示
                fontSize = 16.sp
            )

            repeat(languages.size){ index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .height(40.dp)
                        .clickable {
                            if (currentLanguage != languages[index]) {
                                onLanguageSelect(languages[index])
                                onDismiss()
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = languages[index].displayName(),  // 使用扩展函数获取本地化字符串
                        color = MaterialTheme.colorScheme.surface,  // 使用 primary 颜色，在白色背景上显示
                        fontSize = 16.sp
                    )

                    Image(
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary,),
                        contentScale = ContentScale.FillBounds,
                        painter = if (currentLanguage == languages[index]) painterResource(R.drawable.icon_check_select) else painterResource(R.drawable.icon_check_unselect),
                        contentDescription = stringResource(R.string.settings_language_image_des)
                    )

                }

            }
        }

    }


}