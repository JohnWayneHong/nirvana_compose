package com.ggb.wanandroid.main.data.ai

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_sessions")
data class ChatSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val lastMessage: String,
    val timestamp: Long,
    val messagesJson: String // 存储消息列表的 JSON 字符串
)
