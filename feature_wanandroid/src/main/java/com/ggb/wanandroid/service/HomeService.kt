package com.ggb.wanandroid.service

import com.ggb.wanandroid.data.Article
import com.ggb.wanandroid.data.BannerItem
import com.ggb.wanandroid.data.PageVO
import com.ggb.wanandroid.network.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 首页的相关的api
 * */
interface HomeService {

    @GET("banner/json")
    suspend fun getBanner() : ApiResponse<List<BannerItem>>

    @GET("article/list/{page}/json")
    suspend fun getArticleList(@Path("page") page : Int) : ApiResponse<PageVO<Article>>

    /**
     *置顶文章
     */
    @GET("article/top/json")
    suspend fun getTopArticle() : ApiResponse<List<Article>>
    

}