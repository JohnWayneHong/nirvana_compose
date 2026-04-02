package com.ggb.wanandroid.feature.square.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.blankj.utilcode.util.ToastUtils
import com.ggb.wanandroid.R
import com.ggb.wanandroid.feature.home.ui.ArticleItem
import com.ggb.wanandroid.feature.square.SquareViewModel
import com.ggb.wanandroid.navigation.Routes
import com.ggb.wanandroid.ui.components.RefreshableLazyList
import com.ggb.wanandroid.ui.components.ScrollableTopBar

private val defaultBarsVisibleState = mutableStateOf(true)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SquareScreen(
    viewModel: SquareViewModel = viewModel(),
    navController: NavController,
    onToggleBars: (Boolean) -> Unit = {},
    barsVisible: State<Boolean> = defaultBarsVisibleState,
    drawerState: androidx.compose.material3.DrawerState? = null
) {
    val barsVisibleValue by barsVisible
    val articleList by viewModel.articles.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val listState = rememberLazyListState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    LaunchedEffect(listState) {
        var last = 0
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                val current = index * 100000 + offset
                val delta = current - last
                if (kotlin.math.abs(delta) > 6) {
                    onToggleBars(delta < 0)
                    last = current
                }
                if (index == 0 && offset == 0) {
                    onToggleBars(true)
                }
            }
    }

    val topBarHeight by animateDpAsState(
        targetValue = if (barsVisibleValue) 40.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
        label = "topBarHeight"
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        if (drawerState != null) {
            Box(
                modifier = Modifier
                    .height(topBarHeight)
                    .clipToBounds()
            ) {
                ScrollableTopBar(
                    title = stringResource(R.string.nav_square),
                    drawerState = drawerState,
                    scrollBehavior = scrollBehavior,
                    trailingIcon = R.drawable.icon_add,
                    onTrailingIconClick = {

                        //TODO:跳转分享文章的screen
                                          },
                    barsVisible = barsVisibleValue
                )
            }
        }
        RefreshableLazyList(
            items = articleList,
            modifier = Modifier.fillMaxWidth().weight(1f),
            isLoading = isLoading,
            hasMore = hasMore,
            isRefreshing = isRefreshing,
            listState = listState,
            nestedScrollConnection = scrollBehavior.nestedScrollConnection,
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
                    navController.navigate(Routes.buildLinkRoute(article.title, article.link))
                }
            )
        }
    }
}