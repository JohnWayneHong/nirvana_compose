package com.ggb.wanandroid.feature.search

import com.zfx.commonlib.network.extension.getApiService
import com.zfx.commonlib.network.repository.BaseRepository
import com.zfx.commonlib.network.result.NetworkResult
import com.ggb.wanandroid.data.Article
import com.ggb.wanandroid.data.HotKey
import com.ggb.wanandroid.data.PageVO
import com.ggb.wanandroid.service.SearchService
import kotlinx.coroutines.flow.Flow

class SearchRepository : BaseRepository() {


    private val apiService: SearchService by lazy {
        getApiService<SearchService>()
    }



    /**
     * 分页获取文章数据
     * @param page 从0开始
     * */
    fun getSearchArticleList(page : Int,keyword : String): Flow<NetworkResult<PageVO<Article>>>{
        return requestFlow(
            apiCall = { apiService.getSearchArticles(page,keyword) }
        )
    }


    fun getHotKey() : Flow<NetworkResult<List<HotKey>>>{
        return requestFlow(
            apiCall = {
                apiService.getSearchHotKey()
            }
        )
    }


}