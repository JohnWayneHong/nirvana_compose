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
                        // 【新增】：检查本地是否已经存在完整的最新版 APK
                        if (isApkAlreadyDownloaded(context, updateInfo.versionCode)) {
                            _downloadProgress.value = 100
                        }
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
        val appContext = context.applicationContext

        // 1. 如果进度已经是 100，说明文件已下载，直接拉起安装即可，不走网络请求
        if (_downloadProgress.value == 100) {
            val downloadDir = appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            val apkFile = File(downloadDir, "nirvana_update.apk")
            if (apkFile.exists()) {
                installApkLocal(appContext, apkFile)
            }
            return
        }

        // 如果正在下载中，防止重复点击
        if (_downloadProgress.value in 1..99) return

        viewModelScope.launch(Dispatchers.IO) {
            val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationId = 1001
            val channelId = "update_download_channel"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, "应用更新", NotificationManager.IMPORTANCE_LOW)
                notificationManager.createNotificationChannel(channel)
            }

            // 获取能够唤起当前 App 回到前台的 Intent
            val launchIntent = appContext.packageManager.getLaunchIntentForPackage(appContext.packageName)?.apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                appContext, 0, launchIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notificationBuilder = NotificationCompat.Builder(appContext, channelId)
                .setSmallIcon(com.ggb.wanandroid.R.drawable.icon_article_logo) // 替换为你的应用图标
                .setContentTitle("牛蛙呐 (Nirvana) 正在下载新版本")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)

            try {
                // UI 状态重置为开始下载
                withContext(Dispatchers.Main) { _downloadProgress.value = 1 }

                val downloadDir = appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                val apkFile = File(downloadDir, "nirvana_update.apk")

                // 断点续传核心：获取已下载的长度
                var downloadedLength = 0L
                if (apkFile.exists()) {
                    downloadedLength = apkFile.length()
                }

                val url = URL(apkUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 15000
                connection.readTimeout = 15000

                // 设置断点续传请求头
                if (downloadedLength > 0) {
                    connection.setRequestProperty("Range", "bytes=$downloadedLength-")
                }

                connection.connect()

                val responseCode = connection.responseCode
                val isResume = responseCode == HttpURLConnection.HTTP_PARTIAL // 206 Partial Content

                // 如果服务器不支持断点续传，或者由于某种原因返回了完整的 200 OK，我们需要清空旧文件重新下载
                if (!isResume && responseCode == HttpURLConnection.HTTP_OK) {
                    if (apkFile.exists()) apkFile.delete()
                    downloadedLength = 0L
                } else if (responseCode >= 400) {
                    throw Exception("Server returned code: $responseCode")
                }

                // 计算文件的总大小（本次需要下载的大小 + 已经下载的大小）
                val contentLength = connection.contentLength
                val totalLength = if (contentLength > 0) contentLength + downloadedLength else 0L

                val input = BufferedInputStream(connection.inputStream)
                // 使用 append = true 模式打开输出流，这样新数据会追加到旧数据末尾
                val output = FileOutputStream(apkFile, true)

                val data = ByteArray(8192)
                var total = downloadedLength // total 记录的是文件的总物理进度
                var count: Int

                var lastUpdateTime = System.currentTimeMillis()
                var lastUpdateBytes = downloadedLength

                notificationBuilder.setProgress(100, if (totalLength > 0) (total * 100 / totalLength).toInt() else 0, false)
                notificationManager.notify(notificationId, notificationBuilder.build())

                while (input.read(data).also { count = it } != -1) {
                    total += count
                    output.write(data, 0, count)

                    val currentTime = System.currentTimeMillis()
                    val timeDiff = currentTime - lastUpdateTime

                    // 限制每 500ms 更新一次进度和计算一次速度
                    if (timeDiff >= 500 || (totalLength > 0 && total == totalLength)) {
                        val progress = if (totalLength > 0) (total * 100 / totalLength).toInt() else 0

                        val downloadedInInterval = total - lastUpdateBytes
                        val speedBps = if (timeDiff > 0) (downloadedInInterval * 1000f / timeDiff) else 0f
                        val speedStr = formatSpeed(speedBps)

                        notificationBuilder.setProgress(100, progress, false)
                        notificationBuilder.setContentText("已下载: $progress%  |  $speedStr")
                        notificationManager.notify(notificationId, notificationBuilder.build())

                        withContext(Dispatchers.Main) {
                            _downloadProgress.value = progress.coerceIn(1, 100)
                            _downloadSpeed.value = speedStr
                        }

                        lastUpdateTime = currentTime
                        lastUpdateBytes = total
                    }
                }
                output.flush()
                output.close()
                input.close()

                // 下载完成后的处理
                val installIntent = getInstallIntent(appContext, apkFile)
                val installPendingIntent = PendingIntent.getActivity(
                    appContext, 1, installIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                notificationBuilder.setContentTitle("下载完成")
                    .setContentText("点击立即安装新版本")
                    .setProgress(0, 0, false)
                    .setOngoing(false)
                    .setAutoCancel(true)
                    .setContentIntent(installPendingIntent)
                notificationManager.notify(notificationId, notificationBuilder.build())

                withContext(Dispatchers.Main) {
                    _downloadProgress.value = 100
                    _downloadSpeed.value = ""
                    installApkLocal(appContext, apkFile)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                notificationBuilder.setContentTitle("下载失败")
                    .setContentText("请检查网络后重试")
                    .setProgress(0, 0, false)
                    .setOngoing(false)
                    .setContentIntent(pendingIntent)
                notificationManager.notify(notificationId, notificationBuilder.build())

                withContext(Dispatchers.Main) {
                    _downloadProgress.value = -1
                    _downloadSpeed.value = ""
                }
            }
        }
    }

    // 【辅助方法】：内部拉起安装
    private fun installApkLocal(context: Context, apkFile: File) {
        try {
            val installIntent = getInstallIntent(context, apkFile)
            context.startActivity(installIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 【辅助方法】：获取安装 Intent
    private fun getInstallIntent(context: Context, apkFile: File): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val uri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            // 注意：这里的 authority 必须和你的 AndroidManifest.xml 中配置的 FileProvider 保持绝对一致！
            val authority = "${context.packageName}.fileprovider"
            FileProvider.getUriForFile(context, authority, apkFile)
        } else {
            Uri.fromFile(apkFile)
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive")
        return intent
    }

    // 【辅助方法】：速度格式化
    private fun formatSpeed(bytesPerSecond: Float): String {
        return when {
            bytesPerSecond >= 1024 * 1024 -> String.format(Locale.getDefault(), "%.2f MB/s", bytesPerSecond / (1024 * 1024))
            bytesPerSecond >= 1024 -> String.format(Locale.getDefault(), "%.2f KB/s", bytesPerSecond / 1024)
            else -> String.format(Locale.getDefault(), "%.0f B/s", bytesPerSecond)
        }
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

    // 【新增方法】：校验已下载的 APK 是否完整并且版本号匹配
    @Suppress("DEPRECATION")
    private fun isApkAlreadyDownloaded(context: Context, serverVersionCode: Int): Boolean {
        val downloadDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        val apkFile = File(downloadDir, "nirvana_update.apk")
        if (!apkFile.exists()) return false

        return try {
            val packageManager = context.packageManager
            // 获取本地未安装 APK 的信息，如果包损坏（没下完），这里会返回 null 抛异常
            val packageInfo = packageManager.getPackageArchiveInfo(apkFile.absolutePath, 0)

            val downloadedVersionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo?.longVersionCode?.toInt() ?: -1
            } else {
                packageInfo?.versionCode ?: -1
            }

            // 只有当本地包的版本号与服务器要求的版本号完全一致时，才算作“已下载”
            downloadedVersionCode == serverVersionCode
        } catch (e: Exception) {
            false
        }
    }


}

sealed class UpdateState {
    object Idle : UpdateState()
    object Checking : UpdateState()
    data class HasUpdate(val updateInfo: UpdateInfo) : UpdateState()
    data class Error(val message: String) : UpdateState()
}