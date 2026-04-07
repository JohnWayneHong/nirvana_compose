package com.ggb.wanandroid.main

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ggb.wanandroid.main.update.UpdateState
import com.ggb.wanandroid.main.update.UpdateViewModel
import com.ggb.wanandroid.main.update.ui.UpdateDialog

sealed class MainTab(val route: String, @StringRes val labelRes: Int, val icon: ImageVector) {
    object Home : MainTab("home", R.string.main_nav_home, Icons.Default.Home)
    object Discover : MainTab("discover", R.string.main_nav_discover, Icons.Default.Explore)
    object AIChat : MainTab("ai_chat", R.string.main_nav_ai_chat, Icons.Default.AutoAwesome)
    object Message : MainTab("message", R.string.main_nav_message, Icons.AutoMirrored.Filled.Message)
    object Me : MainTab("me", R.string.main_nav_me, Icons.Default.Person)
    object Search : MainTab("search", R.string.discover_search, Icons.Default.Person)
}

@Composable
fun MainEntryScreen(
    onNavigateToWanAndroid: @Composable () -> Unit
) {
    val context = LocalContext.current
    val updateViewModel: UpdateViewModel = viewModel()
    val updateState by updateViewModel.updateState.collectAsState()

    var selectedTab by remember { mutableStateOf<MainTab>(MainTab.Home) }
    var previousTab by remember { mutableStateOf<MainTab>(MainTab.Home) }
    var showWanAndroidInDiscover by remember { mutableStateOf(false) }

    var isBottomBarVisible by remember { mutableStateOf(true) }
    var lastBackPressTime by remember { mutableLongStateOf(0L) }
    
    val exitMessage = stringResource(R.string.press_again_to_exit)

    // 处理返回键逻辑
    BackHandler {
        if (selectedTab == MainTab.Search) {
            // 如果在搜索页，返回到之前的页面
            selectedTab = previousTab
        } else if (selectedTab != MainTab.Home) {
            // 如果不在首页，先返回首页
            selectedTab = MainTab.Home
        } else {
            // 在首页，双击退出逻辑
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastBackPressTime < 2000) {
                (context as? Activity)?.finish()
            } else {
                lastBackPressTime = currentTime
                Toast.makeText(context, exitMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // 如果是 AI 聊天界面，建议不要滑动隐藏导航栏，以免输入框位置跳动
                if (selectedTab == MainTab.AIChat) {
                    isBottomBarVisible = true
                    return Offset.Zero
                }
                
                if (available.y < -15) {
                    isBottomBarVisible = false
                } else if (available.y > 15) {
                    isBottomBarVisible = true
                }
                return Offset.Zero
            }
        }
    }

    val tabs = remember {
        listOf(MainTab.Home, MainTab.Discover, MainTab.AIChat, MainTab.Message, MainTab.Me)
    }

    LaunchedEffect(Unit) {
        updateViewModel.checkUpdate()
    }

    Scaffold(
        modifier = Modifier.nestedScroll(nestedScrollConnection),
        bottomBar = {
            if (selectedTab != MainTab.Search) {
                AnimatedVisibility(
                    visible = isBottomBarVisible,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    val isDark = isSystemInDarkTheme()
                    val navBarBgColor = if (isDark) Color(0xFF1E2129) else Color.White
                    
                    Surface(
                        color = navBarBgColor,
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                        shadowElevation = 20.dp,
                        tonalElevation = 3.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        NavigationBar(
                            containerColor = Color.Transparent,
                            tonalElevation = 0.dp,
                            modifier = Modifier
                                .navigationBarsPadding()
                                .height(80.dp)
                        ) {
                            tabs.forEach { tab ->
                                val isSelected = selectedTab == tab
                                val activeColor = if (tab == MainTab.AIChat) Color(0xFF00B4D8) else MaterialTheme.colorScheme.primary
                                
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = { 
                                        previousTab = selectedTab
                                        selectedTab = tab 
                                        if (tab != MainTab.Discover) showWanAndroidInDiscover = false
                                        // 切换到 AI 聊天时确保导航栏可见
                                        if (tab == MainTab.AIChat) isBottomBarVisible = true
                                    },
                                    icon = { 
                                        Icon(
                                            imageVector = tab.icon, 
                                            contentDescription = stringResource(tab.labelRes),
                                            tint = if (isSelected) activeColor else Color.Gray.copy(alpha = 0.6f),
                                            modifier = Modifier.size(24.dp)
                                        ) 
                                    },
                                    label = { 
                                        Text(
                                            text = stringResource(tab.labelRes),
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) activeColor else Color.Gray
                                        ) 
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = activeColor.copy(alpha = 0.12f)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(top = if (selectedTab == MainTab.Search) 0.dp else paddingValues.calculateTopPadding())
        ) {
            when (selectedTab) {
                MainTab.Home -> HomeScreen(onSearchClick = { 
                    previousTab = selectedTab
                    selectedTab = MainTab.Search 
                })
                MainTab.Discover -> {
                    if (showWanAndroidInDiscover) {
                        onNavigateToWanAndroid()
                    } else {
                        DiscoverScreen(onEnterWanAndroid = { showWanAndroidInDiscover = true })
                    }
                }
                // 关键修改：传递 paddingValues 以便处理底部遮挡
                MainTab.AIChat -> AIChatScreen(contentPadding = paddingValues)
                MainTab.Me -> MeScreen()
                MainTab.Search -> MainSearchScreen(onBack = { selectedTab = previousTab })
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("${stringResource(selectedTab.labelRes)} ...")
                    }
                }
            }
        }
    }

    when (val state = updateState) {
        is UpdateState.HasUpdate -> {
            UpdateDialog(
                updateInfo = state.updateInfo,
                onDismiss = { updateViewModel.resetState() },
                onConfirm = { url ->
                    updateViewModel.resetState()
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
            )
        }
        else -> {}
    }
}
