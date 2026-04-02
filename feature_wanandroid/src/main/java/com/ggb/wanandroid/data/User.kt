package com.ggb.wanandroid.data

/**
 * 用户信息数据类
 */
data class User(
    val admin: Boolean = false,
    val chapterTops: List<Any> = emptyList(),
    val coinCount: Int = 0,
    val collectIds: List<Int> = emptyList(),
    val email: String = "",
    val icon: String = "",
    val id: Int = 0,
    val nickname: String = "",
    val password: String = "",
    val publicName: String = "",
    val token: String = "",
    val type: Int = 0,
    val username: String = ""
)



