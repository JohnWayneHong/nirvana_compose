package com.ggb.wanandroid.util

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Cookie 拦截器
 * 用于捕获响应头中的 Cookie 并保存到本地
 */
class CookieInterceptor() : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        
        // 获取响应头中的 Set-Cookie
        val setCookieHeaders = response.headers("Set-Cookie")
        
        setCookieHeaders.forEach { cookieHeader ->
            // 解析 loginUserName
            if (cookieHeader.contains("loginUserName=")) {
                val username = extractCookieValue(cookieHeader, "loginUserName")
                username?.let {
                    UserPreferences.saveUsername( it)
                    Log.d("CookieInterceptor","保存用户名到本地sp")
                }
            }
            
            // 解析 token_pass
            if (cookieHeader.contains("token_pass=")) {
                val token = extractCookieValue(cookieHeader, "token_pass")
                token?.let {
                    UserPreferences.saveToken( it)
                    Log.d("CookieInterceptor","保存token到本地sp")
                }
            }
        }
        
        return response
    }
    
    /**
     * 从 Cookie 字符串中提取指定字段的值
     * 例如：从 "loginUserName=zhufx; Expires=Wed, 04-Feb-2026 02:58:24 GMT; Path=/" 中提取 "zhufx"
     */
    private fun extractCookieValue(cookieHeader: String, key: String): String? {
        val keyWithEquals = "$key="
        val startIndex = cookieHeader.indexOf(keyWithEquals)
        if (startIndex == -1) return null
        
        val valueStart = startIndex + keyWithEquals.length
        val endIndex = cookieHeader.indexOf(';', valueStart)
        
        return if (endIndex == -1) {
            cookieHeader.substring(valueStart)
        } else {
            cookieHeader.substring(valueStart, endIndex)
        }
    }
}



