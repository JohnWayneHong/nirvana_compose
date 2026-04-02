package com.ggb.wanandroid.feature.program

import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import com.zfx.commonlib.base.viewmodel.BaseViewModel
import com.zfx.commonlib.ext.collectResult
import com.ggb.wanandroid.data.Article
import com.ggb.wanandroid.data.WeChatAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * author : zhufeixiang
 * date : 2026/1/25
 * des :
 */
class ProgramViewModel : BaseViewModel() {

    private val repository = ProgramRepository()

    private val _programTree = MutableStateFlow<List<WeChatAccount>>(emptyList())
    val programTree : StateFlow<List<WeChatAccount>> = _programTree.asStateFlow()

    private val _isRefreshing = MutableStateFlow<Boolean>(false)
    val isRefreshing : StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private val _isLoadingMore = MutableStateFlow<Boolean>(false)
    val isLoadingMore : StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _hasMore = MutableStateFlow<Boolean>(false)
    val hasMore : StateFlow<Boolean> = _hasMore.asStateFlow()

    private val _programList = MutableStateFlow<List<Article>>(emptyList())
    val programList : StateFlow<List<Article>> = _programList.asStateFlow()

    private var curPage = 0

    var cid = 0

    init {
        getProgramTree()
    }

    private fun getProgramTree() {
        viewModelScope.launch {
            collectResult(
                flow = repository.getProgramTree(),
                onError = { error ->
                    ToastUtils.showShort(error.message)
                },
                onSuccess = { programList ->
                    _programTree.value = programList
                }
            )
        }
    }

    private fun getProgramTreeByCid(){
        viewModelScope.launch {
            collectResult(
                flow = repository.getProgramListsById(curPage,cid),
                onError = { error ->
                    ToastUtils.showShort(error.message)
                },
                onSuccess = { pageTree ->
                    if (_isRefreshing.value){
                        _programList.value = pageTree.datas
                    }else{
                        if (_isLoadingMore.value){
                            _programList.value += pageTree.datas
                        }
                    }

                    _isRefreshing.value = false
                    _isLoadingMore.value = false
                    _hasMore.value = pageTree.hasMore()
                }
            )
        }
    }

    fun setId(id : Int){
        cid = id
    }

    fun refresh(){
        curPage = 0
        _isRefreshing.value = true
        _isLoadingMore.value = false
        getProgramTreeByCid()
    }

    fun loadMore(){
        curPage++
        _isRefreshing.value = false
        _isLoadingMore.value = true
        getProgramTreeByCid()
    }

}