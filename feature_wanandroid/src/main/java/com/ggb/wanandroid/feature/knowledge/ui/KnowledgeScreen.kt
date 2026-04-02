package com.ggb.wanandroid.feature.knowledge.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.ggb.wanandroid.R
import com.ggb.wanandroid.feature.navigation.ui.NavigationScreen
import com.ggb.wanandroid.navigation.Routes
import com.ggb.wanandroid.ui.components.ScrollableTopBar
import kotlinx.coroutines.launch

private val defaultBarsVisibleState = mutableStateOf(true)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnowledgeScreen(
    navController: NavController? = null,
    onToggleBars: (Boolean) -> Unit = {},
    barsVisible: State<Boolean> = defaultBarsVisibleState,
    drawerState: androidx.compose.material3.DrawerState? = null
) {
    val barsVisibleValue by barsVisible
    val context = LocalContext.current
    val tabData = remember {
        listOf(context.getString(R.string.knowledge_system), context.getString(R.string.knowledge_navigation))
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { tabData.size }
    )
    val scope = rememberCoroutineScope()
    // 仅 TopBar 随滚动收起，TabBar 始终吸顶
    val topBarHeight by animateDpAsState(
        targetValue = if (barsVisibleValue) 40.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
        label = "topBarHeight"
    )

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .height(topBarHeight)
                .clipToBounds()
        ) {
            if (drawerState != null) {
                ScrollableTopBar(
                    title = stringResource(R.string.title_knowledge),
                    drawerState = drawerState,
                    scrollBehavior = scrollBehavior,
                    onTrailingIconClick = { navController?.navigate(Routes.SEARCH) },
                    barsVisible = barsVisibleValue
                )
            }
        }
        PrimaryScrollableTabRow(
            modifier = Modifier.height(48.dp),
            selectedTabIndex = pagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            edgePadding = 0.dp,
            indicator = {
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(
                        selectedTabIndex = pagerState.currentPage,
                        matchContentSize = true
                    ),
                    color = MaterialTheme.colorScheme.onPrimary,
                    height = 2.dp
                )
            }
        ) {
            tabData.forEachIndexed { index, title ->
                val selected = pagerState.currentPage == index
                Tab(
                    selected = selected,
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
            key = { page -> page }
        ) { page ->
            if (page == 0) {
                KnowledgePage(navController = navController, onToggleBars = onToggleBars, nestedScrollConnection = null)
            } else {
                NavigationScreen(navController = navController, onToggleBars = onToggleBars, nestedScrollConnection = null)
            }
        }
    }
}