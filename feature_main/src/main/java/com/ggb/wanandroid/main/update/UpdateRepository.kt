package com.ggb.wanandroid.main.update

import com.ggb.commonlib.network.extension.getApiService
import com.ggb.commonlib.network.repository.BaseRepository
import com.ggb.commonlib.network.result.NetworkResult
import com.ggb.wanandroid.main.data.UpdateInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONObject

class UpdateRepository : BaseRepository() {

    private val apiService: UpdateService by lazy {
        getApiService<UpdateService>()
    }

    /**
     * 检查更新
     */
    fun checkUpdate(): Flow<NetworkResult<UpdateInfo>> {
        return requestFlow(
            apiCall = { apiService.checkUpdate() }
        ).map { result ->
            if (result is NetworkResult.Success) {
                val dto = result.data

                // 1. 设置兜底默认值
                var realUpdateContent = dto?.message ?: "发现新版本，建议立即更新体验！"
                var parsedUpdateStyle = "" // 默认为空

                // 2. 尝试解析嵌套的 JSON
                try {
                    val jsonStr = dto?.message
                    // 简单判断一下是不是 JSON 格式，防止后端突然变卦传普通文本
                    if (!jsonStr.isNullOrBlank() && jsonStr.trim().startsWith("{")) {
                        val jsonObject = JSONObject(jsonStr)
                        // 获取真正的更新内容文案
                        realUpdateContent = jsonObject.optString("message", jsonStr)
                        // 【核心修改】：把你的 updateStyle 也提取出来
                        parsedUpdateStyle = jsonObject.optString("updateStyle", "")
                    }
                } catch (e: Exception) {
                    // 解析异常时保持默认值
                }

                val updateInfo = UpdateInfo(
                    versionCode = dto?.versionCode?.toIntOrNull() ?: 0,
                    versionName = dto?.versionName ?: "",
                    updateContent = realUpdateContent,
                    downloadUrl = dto?.downloadUrl ?: "",
                    isForceUpdate = dto?.isForce == 1,
                    updateStyle = parsedUpdateStyle // 【新增】：赋值给 UI 模型
                )

                NetworkResult.Success(updateInfo)
            } else {
                @Suppress("UNCHECKED_CAST")
                result as NetworkResult<UpdateInfo>
            }
        }
    }
}