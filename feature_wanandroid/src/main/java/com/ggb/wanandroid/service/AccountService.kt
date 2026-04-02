package com.ggb.wanandroid.service

import com.ggb.wanandroid.data.AccountBody
import com.ggb.wanandroid.data.User
import com.ggb.wanandroid.feature.coin.data.CoinData
import com.ggb.wanandroid.feature.coin.data.PersonalCoinData
import com.ggb.wanandroid.network.ApiResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface AccountService {

    /**
     * 登录
     * 成功时返回用户信息，失败时返回错误信息（无data）
     * */
    @FormUrlEncoded
    @POST("user/login")
    suspend fun signIn(
        @Field("username") username: String,
        @Field("password") password: String
    ): ApiResponse<User>


    /**
     * 注册
     * */
    @FormUrlEncoded
    @POST("user/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("repassword") repassword: String
    ): ApiResponse<User>


    /**
     * 退出
     * */
    @GET("user/logout/json")
    suspend fun signOut(): ApiResponse<String>


    //获取个人积分，需要登录后访问
    @GET("lg/coin/userinfo/json")
    suspend fun getPersonalCoin() : ApiResponse<CoinData>

}