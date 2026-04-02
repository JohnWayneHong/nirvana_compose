package com.ggb.wanandroid.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.ggb.wanandroid.feature.account.ui.AccountInScreen
import com.ggb.wanandroid.feature.coin.ui.CoinListScreen
import com.ggb.wanandroid.feature.coin.ui.CoinScreen
import com.ggb.wanandroid.feature.home.ui.HomeScreen
import com.ggb.wanandroid.feature.knowledge.ui.KnowledgeDetailScreen
import com.ggb.wanandroid.feature.knowledge.ui.KnowledgeScreen
import com.ggb.wanandroid.feature.link.LinkScreen
import com.ggb.wanandroid.feature.program.ui.ProgramScreen
import com.ggb.wanandroid.feature.search.ui.SearchScreen
import com.ggb.wanandroid.feature.setting.ui.SettingScreen
import com.ggb.wanandroid.feature.square.ui.SquareScreen
import com.ggb.wanandroid.feature.us.AboutUsScreen
import com.ggb.wanandroid.feature.wechat.ui.WechatScreen
import com.ggb.wanandroid.util.KnowledgeItemCache
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * 导航配置参数
 */
data class NavConfig(
    val navController: NavController,
    val onToggleBars: (Boolean) -> Unit = {},
    val barsVisible: State<Boolean> = androidx.compose.runtime.mutableStateOf(true),
    val drawerState: androidx.compose.material3.DrawerState? = null,
    val settingViewModel: com.ggb.wanandroid.feature.setting.SettingViewModel? = null
)

/**
 * 主页面导航配置（Home, Knowledge, Wechat, Square）
 */
fun NavGraphBuilder.setupMainNavigation(config: NavConfig) {
    // Home
    composableWithSlideAnimation(route = Routes.HOME) {
        HomeScreen(
            navController = config.navController,
            onToggleBars = config.onToggleBars,
            barsVisible = config.barsVisible,
            drawerState = config.drawerState
        )
    }

    composableWithSlideAnimation(route = Routes.SQUARE) {
        SquareScreen(
            navController = config.navController,
            onToggleBars = config.onToggleBars,
            barsVisible = config.barsVisible,
            drawerState = config.drawerState
        )
    }

    composableWithSlideAnimation(route = Routes.KNOWLEDGE) {
        KnowledgeScreen(
            navController = config.navController,
            onToggleBars = config.onToggleBars,
            barsVisible = config.barsVisible,
            drawerState = config.drawerState
        )
    }

    composableWithSlideAnimation(route = Routes.WECHAT) {
        WechatScreen(
            navController = config.navController,
            onToggleBars = config.onToggleBars,
            barsVisible = config.barsVisible,
            drawerState = config.drawerState
        )
    }

    composableWithSlideAnimation(route = Routes.PROGRAM) {
        ProgramScreen(
            navController = config.navController,
            onToggleBars = config.onToggleBars,
            barsVisible = config.barsVisible,
            drawerState = config.drawerState
        )
    }

}

/**
 * 详情页导航配置（KnowledgeDetail, Link）
 */
fun NavGraphBuilder.setupDetailNavigation(config: NavConfig) {
    // Knowledge Detail (通过缓存传递 KnowledgeItem)
    composableWithSlideAnimation(route = Routes.KNOWLEDGE_DETAIL) { backStackEntry ->
        // 使用 remember 配合 backStackEntry.id 作为 key，确保每个导航实例有独立的数据
        val knowledgeItem = remember(backStackEntry.id) {
            val item = KnowledgeItemCache.getAndClear()
            // 如果获取到数据，立即重新缓存，这样即使重组也能获取到
            if (item != null) {
                KnowledgeItemCache.cache(item)
            }
            item
        }
        
        // 如果 remember 中的数据为空（可能是重组时），尝试从缓存重新获取
        val finalKnowledgeItem = knowledgeItem ?: KnowledgeItemCache.get()
        
        // 只在首次进入时检查数据有效性，避免返回时重复检查
        LaunchedEffect(backStackEntry.id) {
            if (finalKnowledgeItem == null || finalKnowledgeItem.children.isEmpty()) {
                // 如果数据无效，返回上一页（只在首次进入时检查）
                config.navController.popBackStack()
            } else {
                // 确保数据在缓存中，这样返回时也能获取到
                KnowledgeItemCache.cache(finalKnowledgeItem)
            }
        }
        
        // 显示详情页（只有在数据有效时才显示）
        if (finalKnowledgeItem != null && finalKnowledgeItem.children.isNotEmpty()) {
            KnowledgeDetailScreen(
                data = finalKnowledgeItem,
                navController = config.navController
            )
        }
    }
    
    // Link (带参数)
    composableWithSlideAnimation(route = Routes.LINK) { backStackEntry ->
        val encodedTitle = backStackEntry.arguments?.getString("title") ?: ""
        val encodedLinkUrl = backStackEntry.arguments?.getString("linkUrl") ?: ""
        val title = URLDecoder.decode(encodedTitle, StandardCharsets.UTF_8.toString())
        val linkUrl = URLDecoder.decode(encodedLinkUrl, StandardCharsets.UTF_8.toString())
        LinkScreen(
            title = title,
            linkUrl = linkUrl,
            navController = config.navController,
            onToggleBars = config.onToggleBars
        )
    }
}

/**
 * 其他页面导航配置（Account, AboutUs）
 */
fun NavGraphBuilder.setupOtherNavigation(config: NavConfig) {
    // About Us
    composableWithSlideAnimation(route = Routes.ABOUT_US) {
        AboutUsScreen(navController = config.navController)
    }

    // Account
    composableWithSlideAnimation(route = Routes.ACCOUNT) {
        AccountInScreen(navController = config.navController)
    }


    // Account
    composableWithSlideAnimation(route = Routes.SEARCH) {
        SearchScreen(navController = config.navController)
    }

    // Setting
    composableWithSlideAnimation(route = Routes.SETTING) {
        SettingScreen(
            navController = config.navController,
            viewModel = config.settingViewModel
        )
    }

    // Coin
    composableWithSlideAnimation(route = Routes.COIN_RANK) {
        CoinScreen(
            navController = config.navController
        )
    }

    // CoinDetaiL
    composableWithSlideAnimation(route = Routes.COIN_DETAIL) { backStackEntry ->
        val coinCount = backStackEntry.arguments?.getString("coinCount") ?: ""
        CoinListScreen(
            coinCount = coinCount,
            navController = config.navController
        )
    }
}

