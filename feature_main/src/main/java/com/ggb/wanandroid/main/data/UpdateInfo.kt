package com.ggb.wanandroid.main.data

/**
 * 版本更新信息 (给 UI 显示的最终数据)
 */
data class UpdateInfo(
    val versionCode: Int,
    val versionName: String,
    val updateContent: String,
    val downloadUrl: String,
    val isForceUpdate: Boolean = false,
    // 【新增】：用于控制弹窗样式的标识
    val updateStyle: String = ""
)

/**
 * 接口直接返回的网络数据实体 (专门用来接收后台的奇葩数据格式)
 */
data class UpdateDto(
    val id: String?,
    val versionName: String?,
    val versionCode: String?,
    val downloadUrl: String?,
    val publishDate: String?,
    val isForce: Int?,
    val message: String? // 这里接收到的是一个嵌套的 JSON 字符串
)