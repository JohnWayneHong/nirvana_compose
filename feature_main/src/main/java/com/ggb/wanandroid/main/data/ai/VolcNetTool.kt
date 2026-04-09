package com.ggb.wanandroid.main.data.ai

import android.util.Log
import com.ggb.wanandroid.main.BuildConfig
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class VolcNetTool(
    //请前往local.properties 添加 properties
    //VOLC_API_KEY=xxxx
    private val apiKey: String = BuildConfig.VOLC_API_KEY,
    private val baseUrl: String = "https://ark.cn-beijing.volces.com/"
) {
    private val gson = Gson()
    
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(VolcApiService::class.java)

    /**
     * 流式发送聊天请求 (完全兼容火山 Ark SSE 协议)
     */
    fun sendChatRequestStream(
        modelEndpointId: String = "doubao-seed-1-8-251228",
        messages: List<ChatMessage>
    ): Flow<String> = flow {
        val request = ChatRequest(
            model = modelEndpointId,
            messages = messages,
            stream = true
        )

        val fullUrl = "${baseUrl}api/v3/chat/completions"
        val authHeader = "Bearer $apiKey"

        try {
            val response = apiService.getChatCompletionsStream(fullUrl, authHeader, request).execute()
            
            if (response.isSuccessful) {
                response.body()?.use { body ->
                    val source = body.source()
                    while (!source.exhausted()) {
                        val line = source.readUtf8Line() ?: continue
                        if (line.isBlank()) continue
                        
                        if (line.startsWith("data:")) {
                            val data = line.removePrefix("data:").trim()
                            if (data == "[DONE]") break
                            if (data.isEmpty()) continue
                            
                            try {
                                val chunk = gson.fromJson(data, ChatChunkResponse::class.java)
                                val delta = chunk.choices?.firstOrNull()?.delta
                                val content = delta?.content ?: delta?.reasoningContent
                                
                                if (!content.isNullOrEmpty()) {
                                    emit(content)
                                }
                            } catch (e: Exception) {
                                // 忽略元数据或不完整的 JSON 片段
                            }
                        }
                    }
                }
            } else {
                val errorBody = response.errorBody()?.string() ?: ""
                Log.e("VolcNetTool", "Error: ${response.code()} $errorBody")
                throw Exception("API Error ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("VolcNetTool", "Stream exception", e)
            throw e
        }
    }.flowOn(Dispatchers.IO)
}
