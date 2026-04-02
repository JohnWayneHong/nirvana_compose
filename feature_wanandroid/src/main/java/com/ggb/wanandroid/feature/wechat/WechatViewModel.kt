package com.ggb.wanandroid.feature.wechat

import com.blankj.utilcode.util.ToastUtils
import com.ggb.commonlib.base.viewmodel.BaseViewModel
import com.ggb.commonlib.ext.collectResult
import com.ggb.wanandroid.data.Article
import com.ggb.wanandroid.data.WeChatAccount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WechatViewModel : BaseViewModel() {

    val repository = WechatRepository()
    private val _wechatAccounts = MutableStateFlow<List<WeChatAccount>>(emptyList())
    val wechatAccounts : StateFlow<List<WeChatAccount>> = _wechatAccounts.asStateFlow()


    private val _articleList = MutableStateFlow<List<Article>>(emptyList())
    val articleList : StateFlow<List<Article>> = _articleList.asStateFlow()

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading : StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasMore = MutableStateFlow(true)
    val hasMore: StateFlow<Boolean> = _hasMore.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    var curPage = 1

    var accountId = -1

    init {
        loadData()
    }

    fun setWechatAccountId(id : Int){
        this.accountId = id
    }

    private fun loadData() {
        collectResult(
            flow = repository.getWechatList(),
            onError = { error ->
                val errorMsg = error.message ?: "Unknown error"
                android.util.Log.e("HomeViewModel", "加载文章列表失败: $errorMsg", error.error)
                ToastUtils.showShort(errorMsg)
            },
            onSuccess = { accountList ->
                _wechatAccounts.value = accountList
            }
        )
    }

    /**
     * 刷新数据
     */
    fun refresh() {
        curPage = 1
        getWechatListById(isRefresh = true)
    }

    private fun getWechatListById(isRefresh: Boolean) {
        if (isRefresh){
            _isRefreshing.value = true
        }else{
            _isLoading.value = true
        }
        collectResult(
            flow = repository.getWechatListById(accountId,curPage),
            onSuccess = { data ->
                if (isRefresh){
                    _articleList.value = data.datas
                    _isRefreshing.value = false
                }else{
                    val existingIds = _articleList.value.map { it.id }.toSet()
                    val newArticleList = data.datas.filter { it.id !in  existingIds}
                    _articleList.value += newArticleList
                    _isLoading.value = false
                }
                _hasMore.value = !data.over
            },
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
            }
        )


    }

    /**
     * 加载更多
     */
    fun loadMore() {
        if (_isLoading.value || !_hasMore.value) {
            return
        }
        curPage++
        getWechatListById(isRefresh = false)
    }


}