package com.ggb.wanandroid.main

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ggb.wanandroid.main.update.UpdateState
import com.ggb.wanandroid.main.update.UpdateViewModel
import com.ggb.wanandroid.main.update.ui.UpdateDialog

sealed class MainTab(val route: String, val label: String, val icon: ImageVector) {
    object Home : MainTab("home", "首页", Icons.Default.Home)
    object Discover : MainTab("discover", "发现", Icons.Default.Explore)
    object AIChat : MainTab("ai_chat", "AI对话", Icons.Default.AutoAwesome)
    object Message : MainTab("message", "消息", Icons.Default.Message)
    object Me : MainTab("me", "我的", Icons.Default.Person)
}

@Composable
fun MainEntryScreen(
    onNavigateToWanAndroid: @Composable () -> Unit
) {
    val context = LocalContext.current
    val updateViewModel: UpdateViewModel = viewModel()
    val updateState by updateViewModel.updateState.collectAsState()

    var selectedTab by remember { mutableStateOf<MainTab>(MainTab.Home) }
    var showWanAndroidInDiscover by remember { mutableStateOf(false) }

    // 控制底部导航栏显示隐藏的状态
    var isBottomBarVisible by remember { mutableStateOf(true) }

    // 使用 NestedScrollConnection 来监听滚动方向，实现自动隐藏/显示
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -15) { // 向下滑动查看内容，隐藏导航栏
                    isBottomBarVisible = false
                } else if (available.y > 15) { // 向上滑动查看历史，显示导航栏
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
            AnimatedVisibility(
                visible = isBottomBarVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                val isDark = isSystemInDarkTheme()
                // 深色模式使用更有质感的深蓝灰，浅色模式使用纯白
                val navBarBgColor = if (isDark) Color(0xFF1E2129) else Color.White
                
                Surface(
                    color = navBarBgColor,
                    // 顶部大圆角，底部撑满屏幕，解决“悬浮留白”的问题
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                    shadowElevation = 20.dp,
                    tonalElevation = 3.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        modifier = Modifier
                            .navigationBarsPadding() // 自动适配系统手势/虚拟按键高度，解决偏顶问题
                            .height(80.dp) // 增加高度，使内容垂直居中且不局促
                    ) {
                        tabs.forEach { tab ->
                            val isSelected = selectedTab == tab
                            val activeColor = if (tab == MainTab.AIChat) Color(0xFF00B4D8) else MaterialTheme.colorScheme.primary
                            
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { 
                                    selectedTab = tab 
                                    if (tab != MainTab.Discover) showWanAndroidInDiscover = false
                                },
                                icon = { 
                                    Icon(
                                        imageVector = tab.icon, 
                                        contentDescription = tab.label,
                                        tint = if (isSelected) activeColor else Color.Gray.copy(alpha = 0.6f),
                                        modifier = Modifier.size(24.dp)
                                    ) 
                                },
                                label = { 
                                    Text(
                                        text = tab.label,
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
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(top = paddingValues.calculateTopPadding())
        ) {
            when (selectedTab) {
                MainTab.Home -> HomeScreen()
                MainTab.Discover -> {
                    if (showWanAndroidInDiscover) {
                        onNavigateToWanAndroid()
                    } else {
                        DiscoverScreen(onEnterWanAndroid = { showWanAndroidInDiscover = true })
                    }
                }
                MainTab.AIChat -> AIChatScreen()
                MainTab.Me -> MeScreen()
                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("${selectedTab.label} 模块开发中...")
                    }
                }
            }
        }
    }

    // 更新弹窗
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
