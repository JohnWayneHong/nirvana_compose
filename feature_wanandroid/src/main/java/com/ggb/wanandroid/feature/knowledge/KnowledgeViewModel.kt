package com.ggb.wanandroid.feature.knowledge

import com.blankj.utilcode.util.ToastUtils
import com.zfx.commonlib.base.viewmodel.BaseViewModel
import com.zfx.commonlib.ext.collectResult
import com.zfx.commonlib.ext.collectResultWithLoading
import com.ggb.wanandroid.data.Article
import com.ggb.wanandroid.data.KnowledgeItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class KnowledgeViewModel(val cid: Int = 0) : BaseViewModel() {


    val repository = KnowledgeRepository()

    private val _treeList = MutableStateFlow<List<KnowledgeItem>>(emptyList())
    val treeList : StateFlow<List<KnowledgeItem>> = _treeList.asStateFlow()
    private val _showLoadingDialog = MutableStateFlow(false)
    val showLoadingDialog: StateFlow<Boolean> = _showLoadingDialog.asStateFlow()

    private val _articleList = MutableStateFlow<List<Article>>(emptyList())
    val articleList : StateFlow<List<Article>> = _articleList.asStateFlow()
    private val _loadingMessage = MutableStateFlow("")
    val loadingMessage: StateFlow<String> = _loadingMessage.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    var curPage = 0

    init {
        // 如果有 cid，则加载文章列表；否则加载知识体系树
        if (cid > 0) {
            loadData()
        } else {
            getTree()
        }
    }



    private fun getTree() {
        val loadingMsg = "加载数据..."
        collectResultWithLoading(
            flow = repository.getKnowledgeTree(),
            showLoading = { showMsg ->
                // 显示全局 loading 对话框
                // 如果 showMsg 为空，使用自定义消息；否则使用传入的消息
                _loadingMessage.value = loadingMsg
                _showLoadingDialog.value = true
            },
            dismissLoading = {
                // 隐藏全局 loading 对话框
                _showLoadingDialog.value = false
            },
            onError = { error ->
                ToastUtils.showShort(error.message)
            },
            onSuccess = { treeList ->
                _treeList.value = treeList
            }
        )
    }

    /**
     * 加载数据（初始加载或刷新）
     */
    private fun loadData() {
        curPage = 0
        getArticleListById(isRefresh = true)
    }

    /**
     * 刷新数据
     */
    fun refresh() {
        loadData()
    }

    /**
     * 加载更多
     */
    fun loadMore() {
        if (_isLoading.value || !_hasMore.value) {
            return
        }
        curPage++
        getArticleListById(isRefresh = false)
    }

    private fun getArticleListById(isRefresh: Boolean = false){
        if (isRefresh) {
            _isRefreshing.value = true
        } else {
            _isLoading.value = true
        }
        collectResult(
            flow = repository.getArticleListById(curPage,cid),
            onError = { error ->
                val errorMsg = error.message ?: "Unknown error"
                android.util.Log.e("HomeViewModel", "加载文章列表失败: $errorMsg", error.error)
                ToastUtils.showShort(errorMsg)
                if (isRefresh) {
                    _isRefreshing.value = false
                } else {
                    _isLoading.value = false
                    // 加载失败时，回退页码
                    curPage--
                }
            },
            onSuccess = { knowledge ->
                if (isRefresh) {
                    // 刷新：替换数据
                    _articleList.value = knowledge.datas
                    _isRefreshing.value = false
                } else {
                    // 加载更多：追加数据并去重（根据 ID）
                    val existingIds = _articleList.value.map { it.id }.toSet()
                    val newArticles = knowledge.datas.filter { it.id !in existingIds }
                    _articleList.value = _articleList.value + newArticles
                    _isLoading.value = false
                }
                // 判断是否还有更多数据
                _hasMore.value = !knowledge.over
            }
        )

    }

}