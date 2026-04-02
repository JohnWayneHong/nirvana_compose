package com.ggb.wanandroid.feature.square

import com.blankj.utilcode.util.ToastUtils
import com.ggb.commonlib.base.viewmodel.BaseViewModel
import com.ggb.commonlib.ext.collectResult
import com.ggb.wanandroid.data.Article
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SquareViewModel : BaseViewModel() {


    val repository = SquareRepository()
    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles : StateFlow<List<Article>> = _articles.asStateFlow()


    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading : StateFlow<Boolean> = _isLoading.asStateFlow()


    private val _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing : StateFlow<Boolean> = _isRefreshing.asStateFlow()


    private val _hasMore = MutableStateFlow<Boolean>(true)
    val hasMore : StateFlow<Boolean> = _hasMore.asStateFlow()




    var curPage = 0


    init {
        refresh()
    }

    fun refresh() {
        curPage = 0
        getSquareArticles(true)
    }

    fun loadMore(){
        curPage++
        getSquareArticles(false)
    }

    private fun getSquareArticles(isRefresh : Boolean) {
        if (isRefresh){
            _isRefreshing.value = true
        }else{
            _isLoading.value = true
        }

        collectResult(
            flow = repository.getSquareArticles(curPage),
            onError = {error ->
                curPage--
                ToastUtils.showShort(error.message)
            },
            onSuccess = { data ->
                if (isRefresh){
                    _articles.value = data.datas
                    _isRefreshing.value = false
                }else{
                    _articles.value += data.datas
                    _isLoading.value = false
                }
                _hasMore.value = !data.over
            }
        )


    }
}