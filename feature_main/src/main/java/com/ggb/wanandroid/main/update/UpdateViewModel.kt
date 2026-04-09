package com.ggb.wanandroid.main.update

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewModelScope
import com.ggb.commonlib.base.viewmodel.BaseViewModel
import com.ggb.commonlib.ext.collectResult
import com.ggb.wanandroid.main.data.UpdateInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

class UpdateViewModel(
    private val repository: UpdateRepository = UpdateRepository()
) : BaseViewModel() {

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    private val _downloadProgress = MutableStateFlow(0)
    val downloadProgress: StateFlow<Int> = _downloadProgress.asStateFlow()

    // 【新增 1】：下载速度的 StateFlow
    private val _downloadSpeed = MutableStateFlow("")
    val downloadSpeed: StateFlow<String> = _downloadSpeed.asStateFlow()

    fun checkUpdate(context: Context) {
        viewModelScope.launch {
            _updateState.value = UpdateState.Checking
            collectResult(
                flow = repository.checkUpdate(),
                onSuccess = { updateInfo ->
                    val localVersionCode = getAppVersionCode(context)
                    if (updateInfo.versionCode > localVersionCode) {
                        _updateState.value = UpdateState.HasUpdate(updateInfo)
                    } else {
                        _updateState.value = UpdateState.Idle
                    }
                },
                onError = { error ->
                    _updateState.value = UpdateState.Error(error.message ?: "检查更新失败")
                }
            )
        }
    }

    fun resetState() {
        _updateState.value = UpdateState.Idle
        _downloadProgress.value = 0
        _downloadSpeed.value = "" // 重置时清空速度
    }

    fun downloadAndInstallApk(context: Context, apkUrl: String) {
        if (_downloadProgress.value in 1..99) return

        viewModelScope.launch(Dispatchers.IO) {
            // 使用 applicationContext 防止内存泄漏
            val appContext = context.applicationContext
            val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationId = 1001
            val channelId = "update_download_channel"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, "应用更新", NotificationManager.IMPORTANCE_LOW)
                notificationManager.createNotificationChannel(channel)
            }

            // 【新增 1】：获取能够唤起当前 App 回到前台的 Intent (解决点击回到弹窗)
            val launchIntent = appContext.packageManager.getLaunchIntentForPackage(appContext.packageName)?.apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                appContext, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // 初始化通知构建器
            val notificationBuilder = NotificationCompat.Builder(appContext, channelId)
                .setSmallIcon(com.ggb.wanandroid.R.drawable.icon_article_logo)
                .setContentTitle("牛蛙呐 (Nirvana) 正在下载新版本")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent) // 绑定点击回到 App 的事件

            try {
                _downloadProgress.value = 1

                val downloadDir = appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                val apkFile = File(downloadDir, "nirvana_update.apk")
                if (apkFile.exists()) apkFile.delete()

                val connection = URL(apkUrl).openConnection() as HttpURLConnection
                connection.connectTimeout = 15000
                connection.readTimeout = 15000
                connection.connect()

                val fileLength = connection.contentLength
                val input = BufferedInputStream(connection.inputStream)
                val output = FileOutputStream(apkFile)

                // 【修改 2】：稍微增大缓冲区到 8192，有助于更稳定的测速
                val data = ByteArray(8192)
                var total: Long = 0
                var count: Int

                // 【修改 3】：用于测速的时间和字节记录
                var lastUpdateTime = System.currentTimeMillis()
                var lastUpdateBytes = 0L

                notificationBuilder.setProgress(100, 0, false)
                notificationManager.notify(notificationId, notificationBuilder.build())

                while (input.read(data).also { count = it } != -1) {
                    total += count
                    output.write(data, 0, count)

                    val currentTime = System.currentTimeMillis()
                    val timeDiff = currentTime - lastUpdateTime

                    // 【核心逻辑】：为了防止 UI 卡顿，限制每 500ms 更新一次进度和计算一次速度
                    if (timeDiff >= 500 || (fileLength > 0 && total == fileLength.toLong())) {
                        val progress = if (fileLength > 0) (total * 100 / fileLength).toInt() else 0

                        // 计算这 500ms 内的平均速度
                        val downloadedBytes = total - lastUpdateBytes
                        val speedBps = if (timeDiff > 0) (downloadedBytes * 1000f / timeDiff) else 0f
                        val speedStr = formatSpeed(speedBps)

                        // 顺便让状态栏通知也能看到下载速度，体验拉满！
                        notificationBuilder.setProgress(100, progress, false)
                        notificationBuilder.setContentText("已下载: $progress%  |  $speedStr")
                        notificationManager.notify(notificationId, notificationBuilder.build())

                        withContext(Dispatchers.Main) {
                            _downloadProgress.value = progress.coerceAtLeast(1)
                            _downloadSpeed.value = speedStr
                        }

                        // 重置测速标记
                        lastUpdateTime = currentTime
                        lastUpdateBytes = total
                    }
                }
                output.flush()
                output.close()
                input.close()

                // 【新增 2】：生成安装 APK 的 Intent
                val installIntent = getInstallIntent(appContext, apkFile)
                val installPendingIntent = PendingIntent.getActivity(
                    appContext, 1, installIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                // 【新增 3】：下载完成，更新通知为可点击的安装提示（完美兜底后台限制）
                notificationBuilder.setContentTitle("下载完成")
                    .setContentText("点击立即安装新版本")
                    .setProgress(0, 0, false)
                    .setOngoing(false) // 允许划掉
                    .setAutoCancel(true) // 点击后自动消失
                    .setContentIntent(installPendingIntent) // 绑定点击拉起安装
                notificationManager.notify(notificationId, notificationBuilder.build())

                withContext(Dispatchers.Main) {
                    _downloadProgress.value = 100
                    try {
                        // 尝试直接拉起安装（如果 App 在前台会成功，在后台会被系统拦截，由上面的通知兜底）
                        appContext.startActivity(installIntent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    resetState() // 安装界面出来后，把 App 内的弹窗关掉
                }

            } catch (e: Exception) {
                e.printStackTrace()
                notificationBuilder.setContentTitle("下载失败")
                    .setContentText("请检查网络后重试")
                    .setProgress(0, 0, false)
                    .setOngoing(false)
                    // 下载失败时，点击通知还是回到 App
                    .setContentIntent(pendingIntent)
                notificationManager.notify(notificationId, notificationBuilder.build())

                withContext(Dispatchers.Main) {
                    _downloadProgress.value = -1
                }
            }
        }
    }

    // 【新增 4】：字节/秒 转换为易读的 MB/s 或 KB/s 的工具函数
    private fun formatSpeed(bytesPerSecond: Float): String {
        return when {
            bytesPerSecond >= 1024 * 1024 -> String.format(Locale.getDefault(), "%.2f MB/s", bytesPerSecond / (1024 * 1024))
            bytesPerSecond >= 1024 -> String.format(Locale.getDefault(), "%.2f KB/s", bytesPerSecond / 1024)
            else -> String.format(Locale.getDefault(), "%.0f B/s", bytesPerSecond)
        }
    }

    // 【抽取出的通用安装 Intent 构建器】
    private fun getInstallIntent(context: Context, apkFile: File): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val authority = "${context.packageName}.fileprovider"
            FileProvider.getUriForFile(context, authority, apkFile)
        } else {
            Uri.fromFile(apkFile)
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        return intent
    }

    @Suppress("DEPRECATION")
    private fun getAppVersionCode(context: Context): Int {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                packageInfo.versionCode
            }
        } catch (e: android.content.pm.PackageManager.NameNotFoundException) {
            0
        }
    }
}

sealed class UpdateState {
    object Idle : UpdateState()
    object Checking : UpdateState()
    data class HasUpdate(val updateInfo: UpdateInfo) : UpdateState()
    data class Error(val message: String) : UpdateState()
}