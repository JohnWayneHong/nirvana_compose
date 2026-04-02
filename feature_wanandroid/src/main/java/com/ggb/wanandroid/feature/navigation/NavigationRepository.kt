package com.ggb.wanandroid.feature.navigation

import com.ggb.commonlib.network.extension.getApiService
import com.ggb.commonlib.network.repository.BaseRepository
import com.ggb.commonlib.network.result.NetworkResult
import com.ggb.wanandroid.data.NavigationJson
import com.ggb.wanandroid.service.NavigationService
import kotlinx.coroutines.flow.Flow

class NavigationRepository : BaseRepository() {


    val apiService by lazy {
        getApiService<NavigationService>()
    }


    fun getNavigationJson() : Flow<NetworkResult<List<NavigationJson>>>{
        return requestFlow(
            apiCall = {
                apiService.getNavigationJson()
            }
        )
    }

}