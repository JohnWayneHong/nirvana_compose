package com.ggb.wanandroid.service

import com.ggb.wanandroid.data.NavigationJson
import com.ggb.wanandroid.network.ApiResponse
import retrofit2.http.GET

interface NavigationService {


    @GET("navi/json")
    suspend fun getNavigationJson() : ApiResponse<List<NavigationJson>>
}