package com.ggb.wanandroid.feature.coin

import com.blankj.utilcode.util.ToastUtils
import com.ggb.commonlib.ext.collectResult
import com.ggb.wanandroid.core.BasePageViewModel
import com.ggb.wanandroid.feature.coin.data.CoinData
import com.ggb.wanandroid.feature.coin.data.PersonalCoinData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CoinViewModel : BasePageViewModel() {

    val coinRepository = CoinRepository()

    private val _coinList = MutableStateFlow<List<CoinData>>(emptyList())
    val coinList : StateFlow<List<CoinData>> = _coinList.asStateFlow()

    private val _personalCoinList = MutableStateFlow<List<PersonalCoinData>>(emptyList())
    val personalCoinList : StateFlow<List<PersonalCoinData>> = _personalCoinList.asStateFlow()

    var curPage = 1

    fun refresh(isPersonal : Boolean){
        _isRefreshing.value = true
        _isLoading.value = false
        curPage = 1
        if (isPersonal){
            loadPersonalData()
        }else{
            loadData()
        }
    }

    fun loadMore(isPersonal : Boolean){
        _isRefreshing.value = false
        _isLoading.value = true
        curPage++
        if (isPersonal){
            loadPersonalData()
        }else{
            loadData()
        }
    }

    private fun loadPersonalData() {
        collectResult(
            flow = coinRepository.getPersonalCoinList(curPage),
            onSuccess = { pageData ->
                if (_isRefreshing.value) {
                    _personalCoinList.value = pageData.datas
                    _isRefreshing.value = false
                } else if (_isLoading.value) {
                    _personalCoinList.value += pageData.datas
                    _isLoading.value = false
                }
                _hasMore.value = !pageData.over
            },
            onError = { error ->
                ToastUtils.showShort(error.message)
                if (_isRefreshing.value) _isRefreshing.value = false
                if (_isLoading.value) {
                    _isLoading.value = false
                    if (curPage > 1) curPage--
                }
            }
        )
    }

    private fun loadData() {
        collectResult(
            flow = coinRepository.getCoinRank(curPage),
            onSuccess = { pageData ->
                if (_isRefreshing.value) {
                    _coinList.value = pageData.datas
                    _isRefreshing.value = false
                } else if (_isLoading.value) {
                    _coinList.value += pageData.datas
                    _isLoading.value = false
                }
                _hasMore.value = !pageData.over
            },
            onError = { error ->
                ToastUtils.showShort(error.message)
                if (_isRefreshing.value) _isRefreshing.value = false
                if (_isLoading.value) {
                    _isLoading.value = false
                    if (curPage > 1) curPage--
                }
            }
        )
    }
}