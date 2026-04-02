package com.ggb.wanandroid.util

import com.ggb.wanandroid.data.KnowledgeItem

/**
 * KnowledgeItem 临时缓存
 * 用于在导航时传递 KnowledgeItem 对象
 */
object KnowledgeItemCache {
    private var cachedItem: KnowledgeItem? = null
    
    /**
     * 缓存 KnowledgeItem
     */
    fun cache(item: KnowledgeItem) {
        cachedItem = item
    }
    
    /**
     * 获取并清除缓存的 KnowledgeItem
     */
    fun getAndClear(): KnowledgeItem? {
        val item = cachedItem
        cachedItem = null
        return item
    }
    
    /**
     * 获取缓存的 KnowledgeItem（不清除）
     */
    fun get(): KnowledgeItem? = cachedItem
    
    /**
     * 清除缓存
     */
    fun clear() {
        cachedItem = null
    }
}

