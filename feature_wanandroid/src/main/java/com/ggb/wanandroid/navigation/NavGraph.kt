package com.ggb.wanandroid.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ggb.wanandroid.feature.account.ui.AccountInScreen
import com.ggb.wanandroid.feature.home.ui.HomeScreen
import com.ggb.wanandroid.feature.knowledge.ui.KnowledgeDetailScreen
import com.ggb.wanandroid.feature.knowledge.ui.KnowledgeScreen
import com.ggb.wanandroid.feature.link.LinkScreen
import com.ggb.wanandroid.feature.us.AboutUsScreen
import com.ggb.wanandroid.feature.wechat.ui.WechatScreen
import com.ggb.wanandroid.util.KnowledgeItemCache
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * 路由常量
 */
object Routes {
    const val HOME = "home"
    const val KNOWLEDGE = "knowledge"
    const val PROGRAM = "program"

    const val KNOWLEDGE_DETAIL = "knowledge_detail"
    const val LINK = "link/{title}/{linkUrl}"
    const val ABOUT_US = "aboutus"

    const val ACCOUNT = "account"

    const val WECHAT = "wechat"

    const val SQUARE = "square"

    const val SEARCH = "search"

    const val SETTING = "setting"

    const val COIN_RANK = "coin_rank"

    const val COIN_DETAIL = "coin_detail/{coinCount}"

    // 需要显示底部导航栏的路由
    val routesWithBottomBar = setOf(HOME, KNOWLEDGE,WECHAT,SQUARE,PROGRAM)
    
    // 需要显示全局 TopBar 的路由（这些页面使用全局 TopBar，其他页面自己管理 TopBar）
    val routesWithGlobalTopBar = setOf(HOME, KNOWLEDGE, WECHAT,SQUARE,PROGRAM)
    
    /**
     * 构建带参数的 Link 路由
     */
    fun buildLinkRoute(title: String, linkUrl: String): String {
        val encodedTitle = java.net.URLEncoder.encode(title, java.nio.charset.StandardCharsets.UTF_8.toString())
        val encodedLink = java.net.URLEncoder.encode(linkUrl, java.nio.charset.StandardCharsets.UTF_8.toString())
        return "link/$encodedTitle/$encodedLink"
    }

    /**
     * 构建积分详情页路由（携带 coinCount 参数）
     */
    fun buildCoinDetailRoute(coinCount: String): String = "coin_detail/$coinCount"
}

/**
 * 共享的动画配置
 */
object NavAnimations {
    // 进入动画：从右侧滑入（前进时）
    val slideEnterTransition: EnterTransition = slideInHorizontally(
        initialOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(durationMillis = 400)
    )
    
    // 退出动画：向右侧滑出（前进时）
    val slideExitTransition: ExitTransition = slideOutHorizontally(
        targetOffsetX = { fullWidth -> fullWidth },
        animationSpec = tween(durationMillis = 400)
    )
    
    // 返回进入动画：从左侧滑入（返回时）
    val slidePopEnterTransition: EnterTransition = slideInHorizontally(
        initialOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(durationMillis = 400)
    )
    
    // 返回退出动画：向左侧滑出（返回时）
    val slidePopExitTransition: ExitTransition = slideOutHorizontally(
        targetOffsetX = { fullWidth -> -fullWidth },
        animationSpec = tween(durationMillis = 400)
    )
}

/**
 * 扩展函数：简化 composable 注册（带默认动画）
 */
fun NavGraphBuilder.composableWithSlideAnimation(
    route: String,
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        // 前进时的动画：新页面从右侧滑入，旧页面向右侧滑出
        enterTransition = { NavAnimations.slideEnterTransition },
        exitTransition = { NavAnimations.slideExitTransition },
        // 返回时的动画：当前页面向左侧滑出，上一页面从左侧滑入
        popEnterTransition = { NavAnimations.slidePopEnterTransition },
        popExitTransition = { NavAnimations.slidePopExitTransition }
    ) { backStackEntry ->
        content(backStackEntry)
    }
}

private val defaultBarsVisibleState = mutableStateOf(true)

/**
 * 注册所有路由
 * 使用模块化的方式组织导航配置，提高可维护性
 */
fun NavGraphBuilder.setupNavigation(
    navController: NavController,
    onToggleBars: (Boolean) -> Unit = {},
    barsVisible: State<Boolean> = defaultBarsVisibleState,
    drawerState: androidx.compose.material3.DrawerState? = null,
    settingViewModel: com.ggb.wanandroid.feature.setting.SettingViewModel? = null
) {
    val config = NavConfig(
        navController = navController,
        onToggleBars = onToggleBars,
        barsVisible = barsVisible,
        drawerState = drawerState,
        settingViewModel = settingViewModel
    )
    
    // 按功能模块组织导航配置
    setupMainNavigation(config)      // 主页面（Home, Knowledge, Wechat）
    setupDetailNavigation(config)    // 详情页（KnowledgeDetail, Link）
    setupOtherNavigation(config)      // 其他页面（Account, AboutUs）
}

