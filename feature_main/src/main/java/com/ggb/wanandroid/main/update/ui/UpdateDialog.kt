package com.ggb.wanandroid.main.update.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.ggb.wanandroid.main.R
import com.ggb.wanandroid.main.data.UpdateInfo
import kotlin.io.path.moveTo

@Composable
fun UpdateDialog(
    updateInfo: UpdateInfo,
    downloadProgress: Int,
    downloadSpeed: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    // 【核心新增】：判断当前是否正处于下载中（1% 到 99% 之间）
    val isDownloading = downloadProgress in 1..99

    Dialog(
        onDismissRequest = {
            // 只有非强制更新，且当前没有在下载中，才允许通过物理返回键或点击外部关闭
            if (!updateInfo.isForceUpdate && !isDownloading) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            // 下载期间锁定弹窗，防止误触
            dismissOnBackPress = !updateInfo.isForceUpdate && !isDownloading,
            dismissOnClickOutside = !updateInfo.isForceUpdate && !isDownloading
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.nirvana_update_bg),
                contentDescription = "Update Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize()
            )

            when (updateInfo.updateStyle) {
                "307" -> UpdateStyle307(updateInfo, downloadProgress, downloadSpeed, onDismiss, onConfirm)
                else -> UpdateStyleDefault(updateInfo, downloadProgress, downloadSpeed, onDismiss, onConfirm)
            }
        }
    }
}

@Composable
private fun UpdateStyle307(
    updateInfo: UpdateInfo,
    downloadProgress: Int,
    downloadSpeed: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val isDownloading = downloadProgress in 1..99

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xCC00C6FF), Color(0xCC0072FF))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            NirvanaLogo(modifier = Modifier.size(64.dp))

            // 【修改点】：如果正在下载中，隐藏右上角的关闭按钮，防止误触
            if (!updateInfo.isForceUpdate && !isDownloading && downloadProgress != 100) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.3f))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.common_close),
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.common_discover_new_version),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = updateInfo.versionName,
                    fontSize = 13.sp,
                    color = Color(0xFF0072FF),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF0072FF).copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 180.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = updateInfo.updateContent,
                    fontSize = 14.sp,
                    color = Color.White,
                    lineHeight = 22.sp,
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (downloadProgress > 0) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    val progressText = when (downloadProgress) {
                        -1 -> "下载失败，请重试"
                        100 -> "下载完成，正在安装..."
                        else -> "正在下载... $downloadProgress%"
                    }
                    val progressColor = if (downloadProgress == -1) Color.Red else Color(0xFF0072FF)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = progressText, fontSize = 13.sp, color = progressColor, fontWeight = FontWeight.Bold)
                        if (downloadProgress in 1..99 && downloadSpeed.isNotEmpty()) {
                            Text(text = downloadSpeed, fontSize = 13.sp, color = progressColor, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    val fraction = if (downloadProgress == -1) 1f else (downloadProgress / 100f)
                    LinearProgressIndicator(
                        progress = { fraction },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = progressColor,
                        trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )

                    // 【核心修改】：失败状态显示重试，100% 状态显示立即安装
                    if (downloadProgress == -1) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { onConfirm(updateInfo.downloadUrl) },
                            modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0072FF))
                        ) {
                            Text("点击重试")
                        }
                    } else if (downloadProgress == 100) {
                        Spacer(modifier = Modifier.height(16.dp))
                        // 【修改点】：使用 Box 容器并设置 contentAlignment 为 Center 来实现居中
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = { onConfirm(updateInfo.downloadUrl) },
                                modifier = Modifier
                                    .height(44.dp)
                                    .fillMaxWidth(0.7f), // 稍微宽一点，更好看
                                shape = RoundedCornerShape(22.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0072FF))
                            ) {
                                Text("立即安装", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!updateInfo.isForceUpdate) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.height(40.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFFFF))
                        ) {
                            Text(
                                text = stringResource(id = R.string.common_no_upgrade),
                                color = Color(0xFF0072FF)
                            )
                        }

                        Button(
                            onClick = { onConfirm(updateInfo.downloadUrl) },
                            modifier = Modifier.height(40.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0072FF))
                        ) {
                            Text(text = stringResource(id = R.string.common_upgrade_now))
                        }
                    } else {
                        Button(
                            onClick = { onConfirm(updateInfo.downloadUrl) },
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0072FF))
                        ) {
                            Text(text = stringResource(id = R.string.common_upgrade_now))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NirvanaLogo(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(Color.White.copy(alpha = 0.9f), Color.White.copy(alpha = 0.3f))
            ),
            radius = w / 2f,
            style = Stroke(width = 2.dp.toPx())
        )

        val path = Path().apply {
            moveTo(w * 0.32f, h * 0.68f)
            lineTo(w * 0.32f, h * 0.32f)
            lineTo(w * 0.68f, h * 0.68f)
            lineTo(w * 0.68f, h * 0.32f)
        }

        drawPath(
            path = path,
            color = Color.White,
            style = Stroke(
                width = 5.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        drawCircle(
            color = Color.White,
            radius = 3.5.dp.toPx(),
            center = Offset(w * 0.68f, h * 0.32f)
        )
    }
}

@Composable
private fun UpdateStyleDefault(
    updateInfo: UpdateInfo,
    downloadProgress: Int,
    downloadSpeed: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(id = R.string.common_discover_new_version),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = "${updateInfo.versionName} (${updateInfo.versionCode})",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, bottom = 16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 200.dp)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = updateInfo.updateContent,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (downloadProgress > 0) {
            Column(modifier = Modifier.fillMaxWidth()) {
                val progressText = when (downloadProgress) {
                    -1 -> "下载失败，请重试"
                    100 -> "下载完成，正在安装..."
                    else -> "正在下载... $downloadProgress%"
                }
                val progressColor = if (downloadProgress == -1) Color.Red else Color(0xFF0072FF)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = progressText, fontSize = 13.sp, color = progressColor, fontWeight = FontWeight.Bold)
                    if (downloadProgress in 1..99 && downloadSpeed.isNotEmpty()) {
                        Text(text = downloadSpeed, fontSize = 13.sp, color = progressColor, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                val fraction = if (downloadProgress == -1) 1f else (downloadProgress / 100f)
                LinearProgressIndicator(
                    progress = { fraction },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = progressColor,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )

                // 【核心修改】：失败状态显示重试，100% 状态显示立即安装
                if (downloadProgress == -1) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { onConfirm(updateInfo.downloadUrl) }, modifier = Modifier.align(Alignment.End)) {
                        Text("点击重试")
                    }
                } else if (downloadProgress == 100) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = { onConfirm(updateInfo.downloadUrl) },
                            modifier = Modifier
                                .height(44.dp)
                                .fillMaxWidth(0.8f),
                            shape = RoundedCornerShape(22.dp)
                        ) {
                            Text("立即安装")
                        }
                    }
                }
            }
        } else {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                if (!updateInfo.isForceUpdate) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f).height(40.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(text = stringResource(id = R.string.common_no_upgrade))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                }

                Button(
                    onClick = { onConfirm(updateInfo.downloadUrl) },
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(text = stringResource(id = R.string.common_upgrade_now))
                }
            }
        }
    }
}