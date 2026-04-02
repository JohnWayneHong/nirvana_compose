package com.ggb.wanandroid.feature.home

import com.zfx.commonlib.network.extension.getApiService
import com.zfx.commonlib.network.repository.BaseRepository
import com.zfx.commonlib.network.result.NetworkResult
import com.ggb.wanandroid.data.Article
import com.ggb.wanandroid.data.BannerItem
import com.ggb.wanandroid.data.PageVO
import com.ggb.wanandroid.service.HomeService
import kotlinx.coroutines.flow.Flow

class HomeRepository : BaseRepository(){

    private val apiService: HomeService by lazy {
        getApiService<HomeService>()
    }


    /**
     * 获取banner数据
     * */
    fun getBannerList() : Flow<NetworkResult<List<BannerItem>>> {
        return requestFlow(
            apiCall = { apiService.getBanner() }
        )
    }

    /**
     * 分页获取文章数据
     * @param page 从0开始
     * */
    fun getArticleList(page : Int): Flow<NetworkResult<PageVO<Article>>>{
        return requestFlow(
            apiCall = { apiService.getArticleList(page) }
        )
    }

    /**
    * 分页获取文章数据
    * */
    fun getTopArticle(): Flow<NetworkResult<List<Article>>>{
        return requestFlow(
            apiCall = { apiService.getTopArticle() }
        )
    }

}