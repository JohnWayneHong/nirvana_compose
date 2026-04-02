package com.ggb.wanandroid.feature.setting.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.ggb.wanandroid.R
import com.ggb.wanandroid.ui.theme.AppTheme
import com.ggb.wanandroid.ui.theme.themeName


@Composable
fun ThemeSelectDialog(
    onSelect : (AppTheme) -> Unit,
    onDismiss: () -> Unit = {}
){

    val allThemes = remember {
        AppTheme.allThemes()
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.onPrimary,  // 设置对话框背景色
        shape = RoundedCornerShape(16.dp),  // 设置圆角
        title = {
            Text(
                text = stringResource(R.string.settings_select_theme),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary  // 使用 primary 颜色，在白色背景上显示
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                repeat(allThemes.size){ index ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .height(40.dp)
                            .clickable{
                                onSelect(allThemes[index])
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            modifier = Modifier.weight(1f),
                            text = allThemes[index].themeName(),  // 使用扩展函数获取本地化字符串
                            color = MaterialTheme.colorScheme.surface,  // 使用 primary 颜色，在白色背景上显示
                            fontSize = 14.sp
                        )
                        Row(
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (allThemes[index] == AppTheme.AUTO){
                                Text(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(AppTheme.DARK.color),
                                    text = "",
                                )
                                Text(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(AppTheme.LIGHT.color),
                                    text = "",
                                )
                            }else{
                                Text(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(allThemes[index].color),
                                    text = "",
                                )
                                Text(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(allThemes[index].color),
                                    text = "",
                                )
                            }

                        }
                    }
                    if (index != allThemes.size -1){
                        Spacer(Modifier.size(4.dp))
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )

    )
}