package com.ggb.wanandroid.feature.search

import androidx.lifecycle.viewModelScope
import com.ggb.wanandroid.data.Article
import com.ggb.wanandroid.feature.home.HomeRepository
import com.ggb.commonlib.base.viewmodel.BaseViewModel
import com.ggb.commonlib.ext.collectResult
import com.ggb.commonlib.util.StringResourceHelper
import com.ggb.wanandroid.R
import com.ggb.wanandroid.data.HotKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 搜索页面的 ViewModel
 */
class SearchViewModel(
    private val repository: SearchRepository = SearchRepository()
) : BaseViewModel() {


    private val _hotSearch = MutableStateFlow<List<HotKey>>(emptyList())
    val hotSearch: StateFlow<List<HotKey>> = _hotSearch.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Article>>(emptyList())
    val searchResults: StateFlow<List<Article>> = _searchResults.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // 搜索历史（实际项目中应该持久化到 SharedPreferences 或数据库）
    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory.asStateFlow()


    init {
        getHotKey()
    }

    private fun getHotKey() {
        collectResult(
            flow = repository.getHotKey(),
            onError = { error ->

            },
            onSuccess = { data ->
                _hotSearch.value = data
            }
        )
    }

    /**
     * 执行搜索
     */
    fun search(keyword: String) {
        if (keyword.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            
            collectResult(
                flow = repository.getSearchArticleList(0,keyword),
                onSuccess = { result ->
                    _searchResults.value = result.datas
                    _isLoading.value = false
                },
                onError = { error ->
                    val errorMsg = error.message ?: StringResourceHelper.getString(R.string.search_failed)
                    _isLoading.value = false
                    // 可以显示错误提示
                }
            )
        }
    }
    
    /**
     * 添加到搜索历史
     */
    fun addToHistory(keyword: String) {
        if (keyword.isBlank()) return
        
        val currentHistory = _searchHistory.value.toMutableList()
        // 如果已存在，先移除
        currentHistory.remove(keyword)
        // 添加到最前面
        currentHistory.add(0, keyword)
        // 限制历史记录数量
        if (currentHistory.size > 10) {
            currentHistory.removeAt(currentHistory.size - 1)
        }
        _searchHistory.value = currentHistory
    }
    
    /**
     * 清除搜索历史
     */
    fun clearHistory() {
        _searchHistory.value = emptyList()
    }
}

