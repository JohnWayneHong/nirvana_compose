package com.ggb.wanandroid.core

import com.ggb.commonlib.base.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

open class BasePageViewModel : BaseViewModel() {
    open val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading : StateFlow<Boolean> = _isLoading.asStateFlow()


    open val _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing : StateFlow<Boolean> = _isRefreshing.asStateFlow()


    open val _hasMore = MutableStateFlow<Boolean>(true)
    val hasMore : StateFlow<Boolean> = _hasMore.asStateFlow()

}