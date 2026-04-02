package com.ggb.wanandroid.service

import com.ggb.wanandroid.data.Article
import com.ggb.wanandroid.data.PageVO
import com.ggb.wanandroid.data.WeChatAccount
import com.ggb.wanandroid.network.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface WechatService {


    /**
     * 公众号文章列表
     * */
    @GET("wxarticle/chapters/json")
    suspend fun getWechatList() : ApiResponse<List<WeChatAccount>>

    /**
     * 查看某个公众号历史数据
     * */
    @GET("wxarticle/list/{id}/{pageNum}/json")
    suspend fun getWechatListById(
        @Path("id") id: Int,
        @Path("pageNum") pageNum : Int) : ApiResponse<PageVO<Article>>

}