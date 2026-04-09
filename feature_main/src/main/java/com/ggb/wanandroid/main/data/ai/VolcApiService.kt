package com.ggb.wanandroid.main.data.ai

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface VolcApiService {
    /**
     * 对话补全接口 (非流式 Responses API)
     */
    @POST
    fun getChatCompletions(
        @Url url: String,
        @Header("Authorization") auth: String,
        @Body request: ChatRequest
    ): Call<ChatResponse>

    /**
     * 对话补全接口 (流式 Responses API)
     * 使用 Streaming 注解来处理大响应体
     */
    @Streaming
    @POST
    fun getChatCompletionsStream(
        @Url url: String,
        @Header("Authorization") auth: String,
        @Body request: ChatRequest
    ): Call<ResponseBody>
}
