package com.ggb.wanandroid.util

import com.blankj.utilcode.util.SPUtils

/**
 * 用户信息本地存储工具类
 * 用于保存和读取登录相关的 Cookie 信息
 */
object UserPreferences {
    private const val KEY_USERNAME = "loginUserName"
    private const val KEY_TOKEN = "token_pass"

    private const val KEY_COIN_DATA = "coin_data"

    /**
     * 保存用户名
     */
    fun saveUsername(username: String) {
        SPUtils.getInstance().put(KEY_USERNAME, username)
    }

    /**
     * 保存 Token
     */
    fun saveToken(token: String) {
        SPUtils.getInstance().put(KEY_TOKEN, token)
    }

    /**
     * 获取用户名
     */
    fun getUsername(): String? {
        return SPUtils.getInstance().getString(KEY_USERNAME, null)
    }

    /**
     * 获取 Token
     */
    fun getToken(): String? {
        return SPUtils.getInstance().getString(KEY_TOKEN, null)
    }

    /**
     * 存储 用户积分相关信息
     */
    fun setPersonalCoin(coinData : String){
        SPUtils.getInstance().put(KEY_COIN_DATA,coinData)

    }

    /**
     * 获取 用户积分相关信息
     */
    fun getPersonalCoin() : String?{
        return SPUtils.getInstance().getString(KEY_COIN_DATA,null)
    }

    /**
     * 清除所有用户信息
     */
    fun clear() {
        SPUtils.getInstance().remove(KEY_USERNAME)
        SPUtils.getInstance().remove(KEY_TOKEN)
        SPUtils.getInstance().remove(KEY_COIN_DATA)
    }

    /**
     * 检查是否已登录
     */
    fun isLoggedIn(): Boolean {
        val username = getUsername()
        val token = getToken()
        return !username.isNullOrEmpty() && !token.isNullOrEmpty()
    }
}



