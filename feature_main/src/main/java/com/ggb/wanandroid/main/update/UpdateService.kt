package com.ggb.wanandroid.main.update

import com.ggb.wanandroid.main.data.UpdateDto
import com.ggb.wanandroid.network.ApiResponse
import com.ggb.wanandroid.network.NirvanaResponse
import retrofit2.http.GET
import retrofit2.http.Headers

/**
 * 更新相关的api
 */
interface UpdateService {
    // 加一个自定义 Header 作为标记
    @Headers("Domain-Name: Nirvana")
    @GET("/v2/api/android/apk/latest")
    suspend fun checkUpdate(): NirvanaResponse<UpdateDto>
}