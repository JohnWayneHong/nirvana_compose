package com.ggb.wanandroid.feature.account

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import com.google.gson.Gson
import com.ggb.commonlib.base.viewmodel.BaseViewModel
import com.ggb.commonlib.ext.collectResult
import com.ggb.commonlib.util.StringResourceHelper
import com.ggb.wanandroid.R
import com.ggb.wanandroid.data.AccountBody
import com.ggb.wanandroid.feature.coin.data.CoinData
import com.ggb.wanandroid.feature.coin.data.PersonalCoinData
import com.ggb.wanandroid.util.UserPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay

class AccountViewModel : BaseViewModel() {


    private val TAG = "AccountViewModel"
    private val repository = AccountRepository()


    //登录状态
    private val _signInState = MutableStateFlow(false)
    val signInState : StateFlow<Boolean> = _signInState.asStateFlow()

    //注册状态
    private val _registerState = MutableStateFlow(false)
    val registerState : StateFlow<Boolean> = _registerState.asStateFlow()

    //退出状态
    private val _signOutState = MutableStateFlow(false)
    val signOutState : StateFlow<Boolean> = _signOutState.asStateFlow()


    private val _personalCoin = MutableStateFlow<CoinData>(CoinData())
    val personalCoin : StateFlow<CoinData> = _personalCoin.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading : StateFlow<Boolean> = _isLoading.asStateFlow()



    fun signIn(body : AccountBody){
        _isLoading.value = true
        collectResult(
            flow = repository.signIn(body),
            onError = { error ->
                ToastUtils.showShort(error.message)
                Log.d(TAG,"登录错误返回的错误码 = ${error.code}，结果为${error.message}")
            },
            onSuccess = { user ->
                // 登录成功，保存用户信息
                // 用户名从响应体中获取（因为响应体中有 username）
                ToastUtils.showShort(StringResourceHelper.getString(R.string.account_login_success))
                getPersonalCoin(true)
                user.username?.let {
                    UserPreferences.saveUsername( it)
                    Log.d(TAG,"保存用户名: $it")
                }
                // token_pass 会通过 CookieInterceptor 自动从响应头中提取并保存

                Log.d(TAG,"登录成功，用户信息: $user")
            }
        )
    }


    fun register(body : AccountBody){
        _isLoading.value = true
        collectResult(
            flow = repository.register(body),
            onError = { error ->
                ToastUtils.showShort(error.message)
                Log.d(TAG,"注册错误返回的错误码 = ${error.code}，结果为${error.message}")
            },
            onSuccess = { user ->
                ToastUtils.showShort(StringResourceHelper.getString(R.string.account_register_success))
                getPersonalCoin(false)
                Log.d(TAG,"注册成功，用户信息: $user")
            }
        )
    }

    fun signOut(){
        collectResult(
            flow = repository.signOut(),
            onError = { error ->
                ToastUtils.showShort(error.message)
                Log.d(TAG,"退出错误返回的错误码 = ${error.code}，结果为${error.message}")
            },
            onSuccess = { response ->
                // 退出成功，清除本地保存的用户信息
                UserPreferences.clear()
                _signOutState.value = true
                Log.d(TAG,"退出成功，已清除本地用户信息")
            }
        )
    }

    private fun getPersonalCoin(isLogin : Boolean){
        viewModelScope.launch {
            delay(500)
            collectResult(
                flow = repository.getPersonalCoin(),
                onError = { error ->
                    _isLoading.value = false
                    ToastUtils.showShort(error.message)
                    Log.d(TAG,"个人积分信息的错误码 = ${error.code}，结果为${error.message}")
                },
                onSuccess = { response ->
                    _isLoading.value = false
                    UserPreferences.setPersonalCoin(Gson().toJson(response))
                    if (isLogin){
                        _signInState.value = true
                    }else{
                        _registerState.value = true
                    }
                }
            )
        }
    }



}