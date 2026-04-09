package com.ggb.wanandroid.network
import com.ggb.commonlib.network.response.IBaseResponse
import com.ggb.commonlib.util.StringResourceHelper
import com.ggb.wanandroid.R

/**
 *  * @Author: hwj
 *  * @Description: 专为“牛蛙呐”新后台定制的网络响应包装类
 *  后端字段规范为：code, message, data
 *  * @Date: 2026/4/9 14:57
 */
class NirvanaResponse<T>(
    var data: T? = null,
    var code: Int = -1,       // 新后台叫 code
    var message: String = ""  // 新后台叫 message
) : IBaseResponse<T> {

    // 新后台 code == 0 表示成功
    override fun isSuccess(): Boolean {
        return code == 0
    }

    override fun getDataOrThrow(): T {
        return data ?: throw IllegalStateException(StringResourceHelper.getString(R.string.common_response_empty))
    }

    override fun getDataOrDefault(defaultValue: T): T {
        return data ?: defaultValue
    }

    override fun getErrorMessage(): String {
        return message.ifEmpty { StringResourceHelper.getString(R.string.common_unknown_error) }
    }

    override fun getResponseCode(): Int {
        return code // 返回咱们的 code
    }

    override fun getResponseMsg(): String {
        return message // 返回咱们的 message
    }

    override fun getDataOrNull(): T? {
        return data
    }
}