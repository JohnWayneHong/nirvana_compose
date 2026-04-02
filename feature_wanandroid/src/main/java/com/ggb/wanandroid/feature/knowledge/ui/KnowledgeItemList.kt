package com.ggb.wanandroid.feature.knowledge.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ggb.wanandroid.R
import com.ggb.wanandroid.data.Article
import com.ggb.wanandroid.feature.knowledge.KnowledgeViewModel
import com.ggb.wanandroid.navigation.Routes
import com.ggb.wanandroid.navigation.navigateToLink
import com.ggb.wanandroid.ui.theme.ColorBlack


/**
 * 体系列表组件（支持分页加载和下拉刷新）
 * 使用 Material3 的 PullToRefreshBox 实现下拉刷新
 * 
 * @param modifier 修饰符
 */
@Composable
fun KnowledgeItemList(
    cid: Int,
    navController: NavController,
    modifier: Modifier = Modifier.fillMaxWidth()
) {

    val viewModel = remember(cid) {
        KnowledgeViewModel(cid = cid)
    }

    val articleList by viewModel.articleList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val listState = rememberLazyListState()

    // Material3 的 PullToRefresh 状态
    val pullToRefreshState = rememberPullToRefreshState()

    // 监听滚动到底部，自动加载更多
    val shouldLoadMore by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItemsCount = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0

            // 当滚动到倒数第 3 个 item 时，触发加载更多（提前加载，提升体验）
            totalItemsCount > 0 && lastVisibleItemIndex >= totalItemsCount - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !isLoading && hasMore && !isRefreshing) {
            viewModel.loadMore()
        }
    }

    // 判断是否显示空列表
    val shouldShowEmpty = articleList.isEmpty() && !isLoading && !isRefreshing

    if (shouldShowEmpty) {
        // 空列表状态
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.common_no_data),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        }
    } else {
        // 使用 Material3 的 PullToRefreshBox
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh() },
            state = pullToRefreshState,
            modifier = modifier,
            indicator = {
                Indicator(
                    isRefreshing = isRefreshing,
                    state = pullToRefreshState,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                // 文章列表项
                items(
                    items = articleList,
                    key = { knowledge -> "${knowledge.id}" }
                ) { knowledge ->
                    KnowledgeItemListItem(
                        data = knowledge,
                        cardClick = {
                            navController.navigateToLink(knowledge.title, knowledge.link)
                        }
                    )
                }

                // 加载更多指示器
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                } else if (!hasMore && articleList.isNotEmpty()) {
                    // 没有更多数据提示
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stringResource(R.string.common_no_more_data),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KnowledgeItemListItem(
    data : Article,
    cardClick : () -> Unit
){

    Card(
        modifier = Modifier
            .wrapContentHeight()
            .padding(PaddingValues(horizontal = 12.dp, vertical = 4.dp))
            .clickable { cardClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardColors(
            contentColor = MaterialTheme.colorScheme.tertiary,
            containerColor = MaterialTheme.colorScheme.tertiary,
            disabledContentColor = MaterialTheme.colorScheme.tertiary,
            disabledContainerColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Column(
            modifier = Modifier.padding(PaddingValues(horizontal = 12.dp, vertical = 4.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icon_article_logo),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    contentDescription = stringResource(R.string.article_icon),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = data.niceDate,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onTertiary
                )
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                text = data.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.surface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                Image(
                    modifier = Modifier
                        .size(16.dp),
                    painter = painterResource(id = if(data.collect){
                        R.drawable.icon_heart_blue
                    }else{
                        R.drawable.icon_heart_grey
                    }),
                    contentDescription = "收藏图标"
                )
            }

        }
    }
}

