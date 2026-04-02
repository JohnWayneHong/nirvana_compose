package com.ggb.wanandroid.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ggb.wanandroid.R

/**
 * 支持下拉刷新和上拉加载更多的通用列表组件
 * 使用 Material3 的 PullToRefreshBox 实现下拉刷新
 * 
 * @param T 列表项的数据类型
 * @param modifier 修饰符
 * @param items 列表数据
 * @param isLoading 是否正在加载更多
 * @param hasMore 是否还有更多数据
 * @param isRefreshing 是否正在刷新
 * @param emptyMessage 空列表时显示的消息（默认为"暂无数据"）
 * @param showEmptyWhenLoading 加载中是否显示空列表（默认 false，加载中不显示空列表）
 * @param header 列表头部内容（可选）
 * @param footer 列表底部内容（可选，会显示在加载指示器之后）
 * @param itemKey 为每个列表项生成唯一 key 的函数
 * @param onLoadMore 加载更多回调（当滚动到底部时触发）
 * @param onRefresh 下拉刷新回调
 * @param nestedScrollConnection 嵌套滚动连接（用于与 TopAppBar 联动）
 * @param itemContent 列表项内容
 */
@Composable
fun <T> RefreshableLazyList(
    items: List<T>,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    hasMore: Boolean = true,
    isRefreshing: Boolean = false,
    emptyMessage: String = "",
    showEmptyWhenLoading: Boolean = false,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
    itemKey: ((T) -> Any)? = null,
    onLoadMore: () -> Unit = {},
    onRefresh: () -> Unit = {},
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(bottom = 8.dp),
    nestedScrollConnection: NestedScrollConnection? = null,
    itemContent: @Composable (T) -> Unit
) {
    // Material3 的 PullToRefresh 状态
    val pullToRefreshState = rememberPullToRefreshState()

    // 仅当用户发生过滚动后才允许触发加载更多，避免首屏刚渲染就因「倒数第3个已可见」误触
    var hasUserScrolled by remember { mutableStateOf(false) }
    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) hasUserScrolled = true
    }

    // 监听滚动到底部，自动加载更多
    val shouldLoadMore by remember {
        derivedStateOf {
            if (!hasUserScrolled) return@derivedStateOf false
            val layoutInfo = listState.layoutInfo
            val totalItemsCount = layoutInfo.totalItemsCount
            val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            // 当滚动到倒数第 3 个 item 时，触发加载更多（提前加载，提升体验）
            totalItemsCount > 0 && lastVisibleItemIndex >= totalItemsCount - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !isLoading && hasMore && !isRefreshing) {
            onLoadMore()
        }
    }

    // 判断是否显示空列表
    val isEmpty = items.isEmpty()
    val shouldShowEmpty = isEmpty && (!isLoading || showEmptyWhenLoading) && !isRefreshing

    if (shouldShowEmpty && header == null) {
        // 空列表状态（只有在没有 header 时才显示）
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emptyMessage.ifEmpty { stringResource(R.string.common_no_data) },
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    } else {
        // 使用 Material3 的 PullToRefreshBox
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            state = pullToRefreshState,
            modifier = modifier,
            indicator = {
                PullToRefreshDefaults.Indicator(
                    isRefreshing = isRefreshing,
                    state = pullToRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (nestedScrollConnection != null) {
                            Modifier.nestedScroll(nestedScrollConnection)
                        } else {
                            Modifier
                        }
                    ),
                state = listState,
                contentPadding = contentPadding
            ) {
                // 头部内容（如 Banner）
                if (header != null) {
                    item {
                        header()
                    }
                }

                // 列表项
                items(
                    items = items,
                    key = itemKey
                ) { item ->
                    itemContent(item)
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
                } else if (!hasMore && items.isNotEmpty()) {
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

                // 底部内容（可选）
                if (footer != null) {
                    item {
                        footer()
                    }
                }
            }
        }
    }
}

