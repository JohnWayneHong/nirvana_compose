package com.ggb.wanandroid.feature.wechat

import com.ggb.commonlib.network.extension.getApiService
import com.ggb.commonlib.network.repository.BaseRepository
import com.ggb.commonlib.network.result.NetworkResult
import com.ggb.wanandroid.data.Article
import com.ggb.wanandroid.data.PageVO
import com.ggb.wanandroid.data.WeChatAccount
import com.ggb.wanandroid.service.WechatService
import kotlinx.coroutines.flow.Flow

class WechatRepository : BaseRepository() {
    val apiService by lazy {
        getApiService<WechatService>()
    }



    fun getWechatList() : Flow<NetworkResult<List<WeChatAccount>>> {
        return requestFlow(
            apiCall = { apiService.getWechatList() }
        )
    }


    fun getWechatListById(id : Int,pageNum : Int) : Flow<NetworkResult<PageVO<Article>>> {
        return requestFlow(
            apiCall = { apiService.getWechatListById(id,pageNum) }
        )
    }
}