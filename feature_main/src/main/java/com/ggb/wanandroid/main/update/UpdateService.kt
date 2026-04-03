package com.ggb.wanandroid.main.update

import com.ggb.wanandroid.main.data.UpdateInfo
import com.ggb.wanandroid.network.ApiResponse
import retrofit2.http.GET

/**
 * 更新相关的api
 */
interface UpdateService {
    @GET("update/check/json")
    suspend fun checkUpdate(): ApiResponse<UpdateInfo>
}