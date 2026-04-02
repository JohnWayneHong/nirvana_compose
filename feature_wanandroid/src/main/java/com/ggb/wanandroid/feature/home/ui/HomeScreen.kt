package com.ggb.wanandroid.feature.home.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.blankj.utilcode.util.ToastUtils
import com.ggb.wanandroid.R
import com.ggb.wanandroid.feature.home.HomeViewModel
import com.ggb.wanandroid.data.Article
import com.zfx.commonlib.ext.compose.Banner
import com.ggb.wanandroid.navigation.Routes
import com.ggb.wanandroid.ui.components.RefreshableLazyList
import com.ggb.wanandroid.ui.components.ScrollableTopBar

private val defaultBarsVisibleState = mutableStateOf(true)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    onArticleClick: ((Article) -> Unit)? = null,
    navController: NavController? = null,
    onToggleBars: (Boolean) -> Unit = {},
    barsVisible: State<Boolean> = defaultBarsVisibleState,
    drawerState: androidx.compose.material3.DrawerState? = null
) {
    val barsVisibleValue by barsVisible
    val bannerList by viewModel.bannerList.collectAsState()
    val topArticleList by viewModel.topArticle.collectAsState()
    val articleList by viewModel.articleList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isInitialLoading by viewModel.isInitialLoading.collectAsState()

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
        modifier = modifier
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
                    title = stringResource(R.string.title_wan_android),
                    drawerState = drawerState,
                    scrollBehavior = scrollBehavior,
                    onTrailingIconClick = { navController?.navigate(Routes.SEARCH) },
                    barsVisible = barsVisibleValue
                )
            }
        }
        RefreshableLazyList(
            items = topArticleList + articleList,
            modifier = Modifier.fillMaxWidth().weight(1f),
            isLoading = isLoading,
            hasMore = hasMore,
            isRefreshing = isRefreshing || isInitialLoading,
            listState = listState,
            nestedScrollConnection = scrollBehavior.nestedScrollConnection,
            header = if (bannerList.isNotEmpty()) {
            {
                Banner(
                    modifier = Modifier.height(160.dp),
                    items = bannerList,
                    showIndicator = false,
                    indicator = { curPage, pageCount ->
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            repeat(pageCount) { index ->
                                Spacer(
                                    modifier = Modifier.size(1.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (index == curPage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimary
                                        )
                                )
                            }
                        }
                    },
                    pageContent = { pageItem ->
                        BannerContent(pageData = pageItem)
                    }
                )
            }
        } else null,
        itemKey = { article -> "${article.id}_${article.publishTime}" },
        onLoadMore = { viewModel.loadMore() },
        onRefresh = { viewModel.refresh() }
        ) { article ->
            ArticleItem(
                data = article,
                favoriteClick = {
                    ToastUtils.showShort("收藏文章：${article.title}")
                },
                cardClick = {
                    onArticleClick?.invoke(article)
                    navController?.let { nav ->
                        nav.navigate(Routes.buildLinkRoute(article.title, article.link))
                    }
                }
            )
        }
    }
}