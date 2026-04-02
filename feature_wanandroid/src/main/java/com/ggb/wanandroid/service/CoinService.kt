package com.ggb.wanandroid.service

import com.ggb.wanandroid.data.PageVO
import com.ggb.wanandroid.feature.coin.data.CoinData
import com.ggb.wanandroid.feature.coin.data.PersonalCoinData
import com.ggb.wanandroid.network.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface CoinService {

    //积分排行榜接口
    @GET("coin/rank/{page}/json")
    suspend fun getCoinRank(
        @Path("page") page: Int
    ) : ApiResponse<PageVO<CoinData>>


    //获取个人积分获取列表，需要登录后访问
    @GET("lg/coin/list/{page}/json")
    suspend fun getPersonalCoinList(
        @Path("page") page: Int
    ) : ApiResponse<PageVO<PersonalCoinData>>

}