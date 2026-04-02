package com.ggb.wanandroid.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blankj.utilcode.util.ToastUtils
import androidx.compose.foundation.layout.Box
import com.ggb.wanandroid.R
import kotlinx.coroutines.launch

/**
 * 沉浸式顶/底栏动画包装：根据 barsVisible 做滑入滑出 + 透明度动画
 */
@Composable
fun ImmersiveBar(
    barsVisible: Boolean,
    heightDp: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val heightPx = with(density) { heightDp.toPx() }
    val translationY by animateFloatAsState(
        targetValue = if (barsVisible) 0f else -heightPx,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
        label = "immersiveBarTranslationY"
    )
    val alpha by animateFloatAsState(
        targetValue = if (barsVisible) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
        label = "immersiveBarAlpha"
    )
    Box(
        modifier = modifier.graphicsLayer {
            this.translationY = translationY
            this.alpha = alpha
        }
    ) {
        content()
    }
}

/**
 * 顶部导航栏内容（可复用组件）
 * 用于在 TopAppBar 中显示
 */
@Composable
fun TopBarContent(
    title: String,
    drawerState: androidx.compose.material3.DrawerState,
    onSearchClick: (() -> Unit)? = null
) {
    val scope = rememberCoroutineScope()
    // 在 @Composable 函数体内获取字符串资源
    val searchClickMessage = stringResource(R.string.topbar_click_search)
    
    val actualOnSearchClick = onSearchClick ?: {
        ToastUtils.showShort(searchClickMessage)
    }
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                scope.launch {
                    drawerState.open()
                }
            }
        ) {
            Image(
                painter = painterResource(R.drawable.icon_menu_white),
                contentDescription = stringResource(R.string.topbar_menu),
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = title,
            color = colorResource(R.color.white),
            fontSize = 16.sp
        )

        Spacer(Modifier.weight(1f))

        IconButton(onClick = actualOnSearchClick) {
            Image(
                painter = painterResource(R.drawable.icon_search_white),
                contentDescription = stringResource(R.string.topbar_search),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

/**
 * 顶部导航栏（使用 TopAppBar + nestedScroll，支持整体滚动）
 * @param barsVisible 沉浸式阅读：为 false 时顶栏滑出隐藏（带动画）
 * @param onTrailingIconClick 右侧图标点击；为 null 时不显示右侧图标
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrollableTopBar(
    title: String,
    drawerState: androidx.compose.material3.DrawerState,
    scrollBehavior: TopAppBarScrollBehavior,
    trailingIcon : Int = R.drawable.icon_search_white,
    onTrailingIconClick: (() -> Unit)? = null,
    barsVisible: Boolean = true
) {
    val scope = rememberCoroutineScope()
    val searchClickMessage = stringResource(R.string.topbar_click_search)
    val actualOnTrailingIconClick = onTrailingIconClick ?: {
        ToastUtils.showShort(searchClickMessage)
    }

    val density = LocalDensity.current
    val barHeightPx = with(density) { 40.dp.toPx() }
    val topBarTranslationY by animateFloatAsState(
        targetValue = if (barsVisible) 0f else -barHeightPx,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
        label = "topBarTranslationY"
    )
    val topBarAlpha by animateFloatAsState(
        targetValue = if (barsVisible) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.8f, stiffness = 400f),
        label = "topBarAlpha"
    )

    TopAppBar(
        modifier = Modifier
            .height(40.dp)
            .graphicsLayer {
                translationY = topBarTranslationY
                alpha = topBarAlpha
            },
        title = {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 16.sp
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    scope.launch {
                        drawerState.open()
                    }
                }
            ) {
                Image(
                    painter = painterResource(R.drawable.icon_menu_white),
                    contentDescription = stringResource(R.string.topbar_menu),
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        actions = {
            if (onTrailingIconClick != null) {
                IconButton(onClick = actualOnTrailingIconClick) {
                    Image(
                        painter = painterResource(trailingIcon),
                        contentDescription = stringResource(R.string.topbar_search),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        scrollBehavior = scrollBehavior
    )
}

/**
 * 顶部导航栏（旧版本，保持兼容性）
 */
@Composable
fun TopBar(
    currentRoute: String?,
    drawerState: androidx.compose.material3.DrawerState,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    
    val title = when (currentRoute) {
        com.ggb.wanandroid.navigation.Routes.HOME -> stringResource(R.string.title_wan_android)
        com.ggb.wanandroid.navigation.Routes.KNOWLEDGE -> stringResource(R.string.title_knowledge)
        com.ggb.wanandroid.navigation.Routes.WECHAT -> stringResource(R.string.title_wechat)
        else -> stringResource(R.string.title_wan_android)
    }
    
    TopBarContent(
        title = title,
        drawerState = drawerState
    )
}



