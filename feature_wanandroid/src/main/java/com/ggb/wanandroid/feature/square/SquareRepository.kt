package com.ggb.wanandroid.feature.square

import com.zfx.commonlib.network.extension.getApiService
import com.zfx.commonlib.network.repository.BaseRepository
import com.zfx.commonlib.network.result.NetworkResult
import com.ggb.wanandroid.data.Article
import com.ggb.wanandroid.data.PageVO
import com.ggb.wanandroid.service.SquareService
import kotlinx.coroutines.flow.Flow

class SquareRepository : BaseRepository() {


    val apiService by lazy {
        getApiService<SquareService>()
    }



    fun getSquareArticles(page : Int) : Flow<NetworkResult<PageVO<Article>>> {
        return  requestFlow(
            apiCall = {
                apiService.getSquareArticles(page)
            }
        )
    }



}