package com.ggb.wanandroid.feature.program.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ggb.wanandroid.R
import com.ggb.wanandroid.feature.program.ProgramViewModel
import com.ggb.wanandroid.navigation.Routes
import com.ggb.wanandroid.ui.components.ScrollableTopBar
import kotlinx.coroutines.launch

private val defaultBarsVisibleState = mutableStateOf(true)

/**
 * author : zhufeixiang
 * date : 2026/1/25
 * des :
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramScreen(
    viewModel: ProgramViewModel = viewModel(),
    navController: NavController,
    onToggleBars: (Boolean) -> Unit = {},
    barsVisible: State<Boolean> = defaultBarsVisibleState,
    drawerState: androidx.compose.material3.DrawerState? = null
) {
    val barsVisibleValue by barsVisible
    val programTree by viewModel.programTree.collectAsState()
    val tabData = remember(programTree) {
        programTree.map { it.name }
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val pagerState = rememberPagerState(
        initialPage = 0
    ) {
        tabData.size
    }
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
                    title = stringResource(R.string.nav_program),
                    drawerState = drawerState,
                    scrollBehavior = scrollBehavior,
                    onTrailingIconClick = { navController.navigate(Routes.SEARCH) },
                    barsVisible = barsVisibleValue
                )
            }
        }
        if (tabData.isNotEmpty()) {
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
                tabData.forEachIndexed { index, tabNme ->
                    val selected = index == pagerState.currentPage
                    Tab(
                        selected = selected,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(
                                text = tabNme,
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
                key = { page -> programTree[page].id }
            ) { page ->
                ProgramPage(
                    cid = programTree[page].id,
                    isActive = pagerState.currentPage == page,
                    viewModel = viewModel,
                    navController = navController,
                    onToggleBars = onToggleBars,
                    nestedScrollConnection = null
                )
            }
        }
    }
}