package com.ggb.wanandroid.data

/**
 * 公众号数据类
 */
data class WeChatAccount(
    val articleList: List<Article> = emptyList(),
    val author: String = "",
    val children: List<WeChatAccount> = emptyList(),
    val courseId: Int = 0,
    val cover: String = "",
    val desc: String = "",
    val id: Int = 0,
    val lisense: String = "",
    val lisenseLink: String = "",
    val name: String = "",
    val order: Int = 0,
    val parentChapterId: Int = 0,
    val type: Int = 0,
    val userControlSetTop: Boolean = false,
    val visible: Int = 0
)


