package com.ggb.wanandroid.main.update

import com.ggb.commonlib.network.extension.getApiService
import com.ggb.commonlib.network.repository.BaseRepository
import com.ggb.commonlib.network.result.NetworkResult
import com.ggb.wanandroid.main.data.UpdateInfo
import kotlinx.coroutines.flow.Flow

class UpdateRepository : BaseRepository() {

    private val apiService: UpdateService by lazy {
        getApiService<UpdateService>()
    }

    /**
     * 检查更新
     */
    fun checkUpdate(): Flow<NetworkResult<UpdateInfo>> {
        return requestFlow(
            apiCall = { apiService.checkUpdate() }
        )
    }
}