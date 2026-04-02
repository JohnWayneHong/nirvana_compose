package com.ggb.wanandroid.data

/**
 * 通用分页响应数据类
 * 
 * 用于封装所有分页列表的响应数据，支持泛型，可适用于不同类型的数据列表
 * 
 * @param T 列表项的数据类型，如 Article、Project 等
 * 
 * 使用示例：
 * ```kotlin
 * // API 响应 - 文章列表
 * ApiResponse<PageVO<Article>>
 * 
 * // API 响应 - 项目列表
 * ApiResponse<PageVO<Project>>
 * 
 * // 获取数据
 * val pageData = response.data
 * val items = pageData?.datas ?: emptyList()
 * val hasMore = !pageData?.over ?: false
 * ```
 */
data class PageVO<T>(
    /**
     * 当前页码（从1开始）
     */
    val curPage: Int = 0,

    /**
     * 数据列表
     */
    val datas: List<T> = emptyList(),

    /**
     * 偏移量
     */
    val offset: Int = 0,

    /**
     * 是否已加载完所有数据
     * true: 已加载完，没有更多数据
     * false: 还有更多数据
     */
    val over: Boolean = false,

    /**
     * 总页数
     */
    val pageCount: Int = 0,

    /**
     * 每页大小
     */
    val size: Int = 0,

    /**
     * 总数据量
     */
    val total: Int = 0
) {
    /**
     * 是否还有更多数据
     * @return true 表示还有更多数据，false 表示已加载完
     */
    fun hasMore(): Boolean = !over
    
    /**
     * 是否为空
     * @return true 表示数据列表为空
     */
    fun isEmpty(): Boolean = datas.isEmpty()
    
    /**
     * 获取下一页页码
     * @return 下一页页码，如果没有更多数据则返回当前页
     */
    fun getNextPage(): Int = if (hasMore()) curPage + 1 else curPage
    
    /**
     * 是否为第一页
     * @return true 表示当前是第一页
     */
    fun isFirstPage(): Boolean = curPage == 1
    
    /**
     * 是否为最后一页
     * @return true 表示当前是最后一页
     */
    fun isLastPage(): Boolean = over || curPage >= pageCount
}


