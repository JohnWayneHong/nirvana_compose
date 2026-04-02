package com.ggb.wanandroid.feature.program

import com.zfx.commonlib.network.extension.getApiService
import com.zfx.commonlib.network.repository.BaseRepository
import com.zfx.commonlib.network.result.NetworkResult
import com.ggb.wanandroid.data.Article
import com.ggb.wanandroid.data.PageVO
import com.ggb.wanandroid.data.WeChatAccount
import com.ggb.wanandroid.service.ProgramService
import kotlinx.coroutines.flow.Flow
import java.net.IDN

/**
 * author : zhufeixiang
 * date : 2026/1/24
 * des :
 */
class ProgramRepository : BaseRepository() {

    private val apiService : ProgramService by lazy {
        getApiService<ProgramService>()
    }


    fun getProgramTree() : Flow<NetworkResult<List<WeChatAccount>>>{
        return requestFlow(
            apiCall = {
                apiService.getProgramTree()
            }
        )
    }

    fun getProgramListsById(page : Int,cid : Int) : Flow<NetworkResult<PageVO<Article>>>{
        return requestFlow(
            apiCall = {
                apiService.getProgramsById(page,cid)
            }
        )
    }

}