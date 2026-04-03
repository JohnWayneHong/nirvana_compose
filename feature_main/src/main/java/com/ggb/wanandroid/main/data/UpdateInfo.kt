package com.ggb.wanandroid.main.data

/**
 * 版本更新信息
 */
data class UpdateInfo(
    val versionCode: Int,
    val versionName: String,
    val updateContent: String,
    val downloadUrl: String,
    val isForceUpdate: Boolean = false
)