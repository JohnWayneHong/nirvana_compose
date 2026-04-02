package com.ggb.wanandroid.navigation

import androidx.navigation.NavController
import androidx.navigation.Navigator
import com.ggb.wanandroid.data.KnowledgeItem
import com.ggb.wanandroid.util.KnowledgeItemCache

/**
 * 导航扩展函数 - 类型安全的导航方法
 * 避免字符串错误，提供更好的 IDE 支持
 */

/**
 * 导航到首页
 */
fun NavController.navigateToHome() {
    navigate(Routes.HOME) {
        // 如果已经在首页，不重复导航
        launchSingleTop = true
        // 恢复状态
        restoreState = true
    }
}

/**
 * 导航到知识体系
 */
fun NavController.navigateToKnowledge() {
    navigate(Routes.KNOWLEDGE) {
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * 导航到项目
 */
fun NavController.navigateToProgram() {
    navigate(Routes.PROGRAM) {
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * 导航到公众号
 */
fun NavController.navigateToWechat() {
    navigate(Routes.WECHAT) {
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * 导航到广场
 */
fun NavController.navigateToSquare() {
    navigate(Routes.SQUARE) {
        launchSingleTop = true
        restoreState = true
    }
}

/**
 * 导航到设置
 */
fun NavController.navigateToSetting() {
    navigate(Routes.SETTING)
}

/**
 * 导航到知识详情页
 * @param knowledgeItem 知识体系项（通过缓存传递）
 */
fun NavController.navigateToKnowledgeDetail(knowledgeItem: KnowledgeItem) {
    // 缓存数据
    KnowledgeItemCache.cache(knowledgeItem)
    navigate(Routes.KNOWLEDGE_DETAIL)
}

/**
 * 导航到链接页面
 * @param title 页面标题
 * @param linkUrl 链接地址
 */
fun NavController.navigateToLink(title: String, linkUrl: String) {
    val route = Routes.buildLinkRoute(title, linkUrl)
    navigate(route)
}

/**
 * 导航到关于我们
 */
fun NavController.navigateToAboutUs() {
    navigate(Routes.ABOUT_US)
}

/**
 * 导航到账户页面
 */
fun NavController.navigateToAccount() {
    navigate(Routes.ACCOUNT) {
        launchSingleTop = true
    }
}


/**
 * 导航积分排行
 */
fun NavController.navigateToCoinRank() {
    navigate(Routes.COIN_RANK)
}


/**
 * 积分获取详情（携带 coinCount 参数）
 */
fun NavController.navigateToCoinDetail(coinCount: String) {
    navigate(Routes.buildCoinDetailRoute(coinCount))
}

/**
 * 安全返回上一页
 * @return 是否成功返回（如果已经在根页面，返回 false）
 */
fun NavController.safePopBackStack(): Boolean {
    return if (previousBackStackEntry != null) {
        popBackStack()
        true
    } else {
        false
    }
}

/**
 * 返回到指定路由
 * @param route 目标路由
 * @param inclusive 是否包含目标路由（true 表示弹出目标路由本身）
 */
fun NavController.popBackStackTo(route: String, inclusive: Boolean = false) {
    popBackStack(route, inclusive)
}

/**
 * 返回到首页
 */
fun NavController.popBackStackToHome() {
    popBackStackTo(Routes.HOME, inclusive = false)
}

/**
 * 清除返回栈并导航到指定路由
 * @param route 目标路由
 */
fun NavController.navigateAndClearStack(route: String) {
    navigate(route) {
        // 弹出所有返回栈，只保留目标路由
        popUpTo(0) {
            inclusive = true
        }
    }
}

