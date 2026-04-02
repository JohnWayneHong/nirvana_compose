package com.ggb.wanandroid.feature.wechat.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.blankj.utilcode.util.ToastUtils
import com.ggb.wanandroid.R
import com.ggb.wanandroid.feature.home.ui.ArticleItem
import com.ggb.wanandroid.feature.wechat.WechatViewModel
import com.ggb.wanandroid.navigation.Routes
import com.ggb.wanandroid.navigation.navigateToLink
import com.ggb.wanandroid.ui.components.RefreshableLazyList
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.ui.unit.dp


@Composable
fun WechatPage(
    accountId : Int,
    isActive: Boolean,
    viewModel: WechatViewModel,
    navController : NavController,
    onToggleBars: (Boolean) -> Unit = {},
    nestedScrollConnection: androidx.compose.ui.input.nestedscroll.NestedScrollConnection? = null
){

    // 仅在当前页可见时触发刷新，避免非当前页的重复请求
    LaunchedEffect(accountId, isActive) {
        if (isActive) {
            // 仅当切换到新账号或当前列表为空时刷新
            if (viewModel.accountId != accountId || viewModel.articleList.value.isEmpty()) {
                viewModel.setWechatAccountId(accountId)
                viewModel.refresh()
            }
        }
    }

    val articleList by viewModel.articleList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val listState = rememberLazyListState()

    // 监听滚动，控制 TopBar 和 BottomBar 的显隐
    // 注意：只有在激活状态时才监听，避免非当前页触发
    LaunchedEffect(listState, isActive, onToggleBars) {
        // 只有在激活状态时才监听滚动
        if (!isActive) return@LaunchedEffect
        
        var last = 0
        snapshotFlow { 
            listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset 
        }.collect { (index, offset) ->
            val current = index * 100000 + offset
            val delta = current - last
            // 当滚动超过阈值时，根据滚动方向控制显隐
            if (kotlin.math.abs(delta) > 6) {
                // delta > 0 表示向上滚动（内容向上），隐藏 bars
                // delta < 0 表示向下滚动（内容向下），显示 bars
                onToggleBars(delta < 0)
                last = current
            }
            // 当滚动到顶部时，显示 bars
            if (index == 0 && offset == 0) {
                onToggleBars(true)
            }
        }
    }

    RefreshableLazyList(
        items = articleList,
        modifier = Modifier.fillMaxWidth(),
        isLoading = isLoading,
        hasMore = hasMore,
        isRefreshing = isRefreshing,
        listState = listState,
        nestedScrollConnection = nestedScrollConnection,
        itemKey = { article -> "${article.id}_${article.publishTime}" },
        onLoadMore = { viewModel.loadMore() },
        onRefresh = { viewModel.refresh() }
    ) { article ->
        val articleToast = stringResource(R.string.article_favorite, article.title)
        ArticleItem(
            data = article,
            favoriteClick = {
                ToastUtils.showShort(articleToast)
            },
            cardClick = {
                navController.navigateToLink(article.title, article.link)
            }
        )
    }
}