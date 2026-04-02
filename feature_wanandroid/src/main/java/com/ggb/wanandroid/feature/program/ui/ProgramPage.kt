package com.ggb.wanandroid.feature.program.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.ggb.wanandroid.R
import com.ggb.wanandroid.data.Article
import com.ggb.wanandroid.feature.program.ProgramViewModel
import com.ggb.wanandroid.ui.components.RefreshableLazyList

/**
 * author : zhufeixiang
 * date : 2026/1/25
 * des :
 */

@Composable
fun ProgramPage(
    cid : Int,
    isActive : Boolean,
    viewModel: ProgramViewModel,
    navController: NavController,
    onToggleBars: (Boolean) -> Unit = {},
    nestedScrollConnection: NestedScrollConnection? = null
){


    LaunchedEffect(cid,isActive) {
        if (isActive){
            if (viewModel.cid != cid){
                viewModel.setId(id = cid)
                viewModel.refresh()
            }
        }
    }

    val programList by viewModel.programList.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()

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
        items = programList,
        modifier = Modifier.fillMaxSize(),
        isLoading = isLoadingMore,
        isRefreshing = isRefreshing,
        listState = listState,
        nestedScrollConnection = nestedScrollConnection,
        itemKey = { program -> "${program.id}_${program.userId}"},
        onRefresh = { viewModel.refresh() },
        onLoadMore = { viewModel.loadMore() },
        hasMore = hasMore
    ) { article ->
        val index = programList.indexOf(article)
        ProgramItem( article,index == programList.size - 1)
    }
}

@Composable
fun ProgramItem(
    data : Article,
    isLast : Boolean
){

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 4.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (data.envelopePic.isNotEmpty()){
                AsyncImage(
                    model = data.envelopePic,
                    contentDescription = null,
                    modifier = Modifier
                        .width(60.dp)
                        .height(120.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(
                    modifier = Modifier.width(12.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ){
                    // title
                    Text(
                        modifier = Modifier
                            .weight(1f),
                        text = data.title,
                        fontSize = 8.sp,
                        color = MaterialTheme.colorScheme.surface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // 时间信息
                    Text(
                        modifier = Modifier.padding(start = 8.dp),
                        text = data.niceDate,
                        fontSize = 8.sp,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }
                //描述
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = data.desc,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.surface,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (data.author.isNotEmpty()) {
                            stringResource(R.string.article_author, data.author)
                        } else {
                            stringResource(R.string.article_share_user, data.shareUser)
                        },
                        fontSize = 6.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(
                        modifier = Modifier.weight(1f)
                    )
                    Image(
                        modifier = Modifier
                            .size(16.dp)
                            .clickable { },
                        painter = painterResource(id = if(data.collect){
                            R.drawable.icon_heart_blue
                        }else{
                            R.drawable.icon_heart_grey
                        }),
                        contentDescription = stringResource(R.string.article_favorite_icon)
                    )
                }
            }
        }
        if (!isLast){
            HorizontalDivider(
                Modifier.height(1.dp),
                color = MaterialTheme.colorScheme.onTertiary
            )
        }
    }


}