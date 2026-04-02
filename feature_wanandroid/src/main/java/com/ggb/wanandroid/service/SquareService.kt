package com.ggb.wanandroid.service

import com.ggb.wanandroid.data.Article
import com.ggb.wanandroid.data.PageVO
import com.ggb.wanandroid.network.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface SquareService {


    /**
     * 广场列表数据
     * */
    @GET("user_article/list/{page}/json")
    suspend fun getSquareArticles(@Path("page") page : Int): ApiResponse<PageVO<Article>>


}