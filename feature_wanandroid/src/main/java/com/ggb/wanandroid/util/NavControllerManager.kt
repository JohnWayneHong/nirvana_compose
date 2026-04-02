package com.ggb.wanandroid.util

import androidx.navigation.NavController
import java.lang.ref.WeakReference

/**
 * NavController 管理器
 * 用于在 Application 层访问 NavController 进行导航
 */
object NavControllerManager {
    private var navControllerRef: WeakReference<NavController>? = null

    /**
     * 注册 NavController
     * 在 MainActivity 中调用
     */
    fun registerNavController(navController: NavController) {
        navControllerRef = WeakReference(navController)
    }

    /**
     * 取消注册 NavController
     */
    fun unregisterNavController() {
        navControllerRef = null
    }

    /**
     * 获取当前的 NavController
     */
    fun getNavController(): NavController? {
        return navControllerRef?.get()
    }

    /**
     * 导航到指定路由
     * @return 是否成功导航
     */
    fun navigate(route: String): Boolean {
        val navController = getNavController()
        return if (navController != null) {
            try {
                navController.navigate(route)
                true
            } catch (e: Exception) {
                false
            }
        } else {
            false
        }
    }
}



