package com.ggb.wanandroid.feature.navigation

import androidx.compose.runtime.State
import com.blankj.utilcode.util.ToastUtils
import com.ggb.commonlib.base.viewmodel.BaseViewModel
import com.ggb.commonlib.ext.collectResult
import com.ggb.commonlib.ext.collectResultWithLoading
import com.ggb.wanandroid.data.NavigationJson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationViewModel : BaseViewModel() {

    private val repository = NavigationRepository()


    private val _navigationJson = MutableStateFlow<List<NavigationJson>>(emptyList())
    val navigationJson : StateFlow<List<NavigationJson>> = _navigationJson.asStateFlow()

    private val _showLoading = MutableStateFlow<Boolean>(false)
    val showLoading : StateFlow<Boolean> = _showLoading.asStateFlow()



    init {
        loadData()
    }

    private fun loadData() {
        collectResultWithLoading(
            flow = repository.getNavigationJson(),
            showLoading = { showMsg ->
                _showLoading.value = true
            },
            dismissLoading = {
                _showLoading.value = false
            },
            onSuccess = { data ->
                _navigationJson.value = data
            },
            onError = { error ->
                ToastUtils.showShort(error.message)
            }
        )
    }


}