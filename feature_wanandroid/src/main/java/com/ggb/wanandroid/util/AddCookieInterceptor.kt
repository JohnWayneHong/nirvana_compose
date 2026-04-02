package com.ggb.wanandroid.util

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 请求时自动携带本地保存的 Cookie。
 * 与 [CookieInterceptor] 配合使用：本拦截器在请求头中加上 Cookie，CookieInterceptor 从响应头中保存 Set-Cookie。
 */
class AddCookieInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val cookie = buildCookieHeader() ?: return chain.proceed(request)

        val newRequest = request.newBuilder()
            .header("Cookie", cookie)
            .build()
        return chain.proceed(newRequest)
    }

    /**
     * 拼接 Cookie 请求头，格式：loginUserName=xxx; token_pass=xxx
     */
    private fun buildCookieHeader(): String? {
        val username = UserPreferences.getUsername()
        val token = UserPreferences.getToken()
        if (username.isNullOrEmpty() || token.isNullOrEmpty()) return null
        return "loginUserName=$username; token_pass=$token"
    }
}
