package com.ggb.wanandroid.main.data.ai

import com.google.gson.annotations.SerializedName
import java.util.UUID

/**
 * 火山大模型请求体
 */
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val stream: Boolean = false
)

/**
 * 消息结构 - 增加 id 确保 LazyColumn 渲染稳定性
 */
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: String,
    val content: String
)

/**
 * 响应结构 (非流式)
 */
data class ChatResponse(
    val choices: List<Choice>? = null
)

data class Choice(
    val message: ChatMessage? = null,
    @SerializedName("finish_reason")
    val finishReason: String? = null
)

/**
 * 流式响应结构
 */
data class ChatChunkResponse(
    val choices: List<ChoiceChunk>? = null,
    val usage: Any? = null
)

data class ChoiceChunk(
    val delta: Delta? = null,
    @SerializedName("finish_reason")
    val finishReason: String? = null
)

data class Delta(
    val content: String? = null,
    @SerializedName("reasoning_content")
    val reasoningContent: String? = null,
    val role: String? = null
)
