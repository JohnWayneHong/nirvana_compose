package com.ggb.wanandroid.feature.knowledge.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabIndicatorScope
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ggb.commonlib.ext.compose.Center
import com.ggb.wanandroid.R
import com.ggb.wanandroid.data.KnowledgeItem
import com.ggb.wanandroid.feature.knowledge.KnowledgeViewModel
import kotlinx.coroutines.launch
import kotlin.text.get


/**
 * 体系详情
 * */
@Composable
fun KnowledgeDetailScreen(
    data : KnowledgeItem,
    navController : NavController,
    modifier: Modifier = Modifier.fillMaxSize()
){

    // 使用 remember 缓存 tab 数据，避免每次重组都重新创建
    val tabData = remember(data.children) {
        data.children.map { it.name }
    }

    Scaffold(
        modifier = modifier,

        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary
                    ),
                verticalAlignment = Alignment.CenterVertically
            ){
                Spacer(
                    Modifier.size(12.dp)
                )

                Image(
                    modifier = Modifier
                        .height(24.dp)
                        .width(16.dp)
                        .clickable {
                            navController.popBackStack()
                        },
                    painter = painterResource(R.drawable.icon_back_white),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                    contentDescription = stringResource(R.string.knowledge_detail_back_button)
                )
                Spacer(
                    Modifier.size(12.dp)
                )

                Text(
                    text = data.name,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(
                    Modifier.weight(1f)
                )
                Image(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            },
                    painter = painterResource(R.drawable.icon_search_white),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                    contentDescription = stringResource(R.string.knowledge_detail_search_button)
                )

                Spacer(
                    Modifier.size(12.dp)
                )
                Image(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                        },
                    painter = painterResource(R.drawable.icon_share_white),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                    contentDescription = stringResource(R.string.knowledge_detail_share_button)
                )

                Spacer(
                    Modifier.size(24.dp)
                )

            }
        }

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // 确保有子节点才显示内容
            if (data.children.isNotEmpty()) {
                val pagerState = rememberPagerState(
                    initialPage = 0,
                    pageCount = { data.children.size }
                )
                val scope = rememberCoroutineScope()

                PrimaryScrollableTabRow(
                    modifier = Modifier.height(48.dp),
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    edgePadding = 0.dp, // 移除左右边距
                    // 不设置 minTabWidth，让 Tab 根据内容自适应宽度
                    indicator = {
                        // PrimaryScrollableTabRow 使用 PrimaryIndicator
                        // indicator 是 TabIndicatorScope 的扩展函数，不需要显式参数
                        TabRowDefaults.PrimaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(
                                selectedTabIndex = pagerState.currentPage,
                                matchContentSize = true
                            ),
                            color = MaterialTheme.colorScheme.onPrimary,   // 指示器颜色
                            height = 2.dp                // 指示器高度
                        )
                    }
                ) {
                    tabData.forEachIndexed { index, title ->
                        val selected = index == pagerState.currentPage
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = { 
                                Text(
                                    text = title, 
                                    color = if (selected) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f),
                                    fontSize = 12.sp
                                ) 
                            }
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f),
                    key = { page -> data.children[page].id }
                ) { page ->
                    KnowledgeItemList(
                        cid = data.children[page].id,
                        navController = navController,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }


    }

}