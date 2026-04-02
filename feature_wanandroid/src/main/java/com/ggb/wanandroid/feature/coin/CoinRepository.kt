package com.ggb.wanandroid.feature.coin

import com.ggb.commonlib.network.extension.getApiService
import com.ggb.commonlib.network.repository.BaseRepository
import com.ggb.commonlib.network.result.NetworkResult
import com.ggb.wanandroid.data.PageVO
import com.ggb.wanandroid.feature.coin.data.CoinData
import com.ggb.wanandroid.feature.coin.data.PersonalCoinData
import com.ggb.wanandroid.service.CoinService
import kotlinx.coroutines.flow.Flow

class CoinRepository : BaseRepository() {


    private val apiService by lazy {
        getApiService<CoinService>()
    }


    fun getCoinRank(page : Int) : Flow<NetworkResult<PageVO<CoinData>>>{
        return  requestFlow(
            apiCall = {
                apiService.getCoinRank(page)
            }
        )
    }


    fun getPersonalCoinList(page : Int) : Flow<NetworkResult<PageVO<PersonalCoinData>>>{
        return requestFlow(
            apiCall = {
                apiService.getPersonalCoinList(page)
            }
        )
    }


}