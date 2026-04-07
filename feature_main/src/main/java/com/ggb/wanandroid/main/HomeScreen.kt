package com.ggb.wanandroid.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.zIndex
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onSearchClick: () -> Unit = {}) {
    val articleList = remember { getMockHomeData() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 12.dp)
                            .height(42.dp)
                            .clickable { onSearchClick() },
                        shape = RoundedCornerShape(21.dp),
                        color = Color.White,
                        tonalElevation = 2.dp,
                        shadowElevation = 1.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                stringResource(R.string.home_search_placeholder),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(
                            Icons.Default.NotificationsNone, 
                            contentDescription = stringResource(R.string.home_notification),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item(key = "banner") { HomeBanner() }
            item(key = "quick_actions") { HomeQuickActions() }
            item(key = "featured_columns_header") { 
                SectionHeader(
                    stringResource(R.string.home_featured_columns), 
                    stringResource(R.string.home_view_all)
                ) 
            }
            item(key = "featured_columns_row") { FeaturedColumns() }
            item(key = "recent_updates_header") { 
                SectionHeader(
                    stringResource(R.string.home_recent_updates), 
                    ""
                ) 
            }
            items(items = articleList, key = { it.id }) { item ->
                HomeArticleItem(item)
            }
        }
    }
}

@Composable
private fun HomeBanner() {
    val banners = remember { getMockBannerData() }
    val virtualCount = 5000
    val initialPage = virtualCount / 2 - (virtualCount / 2 % banners.size)
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { virtualCount })

    // 监听拖拽状态：true表示用户手指正在屏幕上滑动
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    // 逻辑：当 isDragged 改变时重启协程。
    // 如果用户停止拖拽 (!isDragged)，则进入 while 循环执行 3s 计时的自动轮播。
    // 如果用户再次开始拖拽，LaunchedEffect 会因 key(isDragged) 变化而取消旧协程，从而停止计时和跳转。
    LaunchedEffect(isDragged) {
        if (!isDragged) {
            while (true) {
                delay(3000)
                // 额外检查：确保在 delay 结束准备跳转时，页面没有处于其他滚动中
                if (!pagerState.isScrollInProgress) {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }
        }
    }

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 36.dp), 
            beyondViewportPageCount = 5, 
            pageSpacing = 0.dp 
        ) { page ->
            val bannerIndex = page % banners.size
            val banner = banners[bannerIndex]
            
            val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
            
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(pageOffset)
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0f, 0.5f)
                        
                        val scalingStep = 0.05f
                        val stepOffset = 6.dp.toPx()
                        
                        if (pageOffset <= 0) {
                            // 当前及后续页面
                            val lerpScale = 1f + (pageOffset * scalingStep).coerceIn(-0.25f, 0f)
                            scaleX = lerpScale
                            scaleY = lerpScale
                            
                            // 关键位移公式修正：(1 + pageOffset) 将后续卡片拉回，- lerpScale 补偿左缩放宽度损失，- pageOffset * stepOffset 产生递进间距
                            translationX = (1f + pageOffset - lerpScale) * size.width - pageOffset * stepOffset
                            
                            alpha = (pageOffset + banners.size).coerceIn(0f, 1f)
                        } else {
                            // 滑过的页面
                            alpha = (1f - pageOffset).coerceIn(0f, 1f)
                            translationX = 0f
                        }
                    },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = if (pageOffset > -0.5f) 4.dp else 2.dp
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(brush = Brush.linearGradient(colors = banner.colors))
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp)
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = banner.tag,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = banner.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        Row(
            Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(banners.size) { iteration ->
                val isSelected = (pagerState.currentPage % banners.size) == iteration
                val color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                
                val width = if (isSelected) 12.dp else 6.dp
                
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(width = width, height = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun HomeQuickActions() {
    val actions = listOf(
        ActionItem(Icons.AutoMirrored.Filled.LibraryBooks, stringResource(R.string.home_action_tutorial), Color(0xFF4CAF50)),
        ActionItem(Icons.Default.Code, stringResource(R.string.home_action_project), Color(0xFF2196F3)),
        ActionItem(Icons.Default.QuestionAnswer, stringResource(R.string.home_action_qa), Color(0xFFFF9800)),
        ActionItem(Icons.Default.EmojiEvents, stringResource(R.string.home_action_contest), Color(0xFFE91E63))
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        actions.forEach { action ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { }
                    .padding(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(action.color.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(action.icon, contentDescription = null, tint = action.color, modifier = Modifier.size(26.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(action.label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, actionText: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (actionText.isNotEmpty()) {
            Text(actionText, color = MaterialTheme.colorScheme.primary, fontSize = 13.sp)
        }
    }
}

@Composable
private fun FeaturedColumns() {
    val columns = listOf(
        stringResource(R.string.home_column_compose),
        stringResource(R.string.home_column_kmp),
        stringResource(R.string.home_column_arch),
        stringResource(R.string.home_column_perf)
    )
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(columns) { column ->
            Surface(
                color = if (isSystemInDarkTheme()) Color(0xFF373737) else Color(0xFFF5F7F9),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.width(130.dp).clickable { }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(column, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        stringResource(R.string.home_article_count, 12), 
                        fontSize = 11.sp, 
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeArticleItem(item: HomeData) {
    val cardBgColor = if (isSystemInDarkTheme()) Color(0xFF2C2C2C) else Color.White

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        color = cardBgColor,
        shape = RoundedCornerShape(12.dp),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .clickable { }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        item.author,
                        color = Color(0xFF1E88E5),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(item.time, color = Color.Gray, fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row {
                    item.tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFE3F2FD), RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(tag, color = Color(0xFF1976D2), fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }
            }
            if (item.hasImage) {
                Spacer(modifier = Modifier.width(12.dp))
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF0F0F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, tint = Color.LightGray)
                }
            }
        }
    }
}

data class ActionItem(val icon: ImageVector, val label: String, val color: Color)

data class HomeData(
    val id: Int,
    val title: String,
    val author: String,
    val time: String,
    val tags: List<String>,
    val hasImage: Boolean = false
)

data class BannerData(
    val id: Int,
    val title: String,
    val tag: String,
    val colors: List<Color>
)

private fun getMockHomeData() = listOf(
    HomeData(1, "Compose 自定义布局详解：从测量到放置", "鸿洋", "20分钟前", listOf("Compose", "自定义")),
    HomeData(2, "Android 15 适配指南：开发者需要关注的重大变更", "Google", "1小时前", listOf("Android 15"), true),
    HomeData(3, "Kotlin 2.0 K2 编译器正式版发布", "JetBrains", "3小时前", listOf("Kotlin")),
    HomeData(4, "使用 Room 和 Hilt 构建 modern 架构", "郭霖", "昨天", listOf("架构", "数据库"), true),
    HomeData(5, "如何在 iOS 上运行 Compose 代码", "Nirvana", "2天前", listOf("KMP", "iOS"))
)

private fun getMockBannerData() = listOf(
    BannerData(1, "Jetpack Compose 性能优化实战技巧", "热门", listOf(Color(0xFF3A7BD5), Color(0xFF00D2FF))),
    BannerData(2, "Kotlin Coroutines 深度解析与最佳实践", "进阶", listOf(Color(0xFF6A11CB), Color(0xFF2575FC))),
    BannerData(3, "Android 15 适配全攻略：开发者必看", "最新", listOf(Color(0xFFFF5F6D), Color(0xFFFFC371))),
    BannerData(4, "Material Design 3 在 Compose 中的深度应用", "设计", listOf(Color(0xFF11998E), Color(0xFF38EF7D))),
    BannerData(5, "打造流畅所在的 KMP 跨平台移动端应用", "前沿", listOf(Color(0xFFEB3349), Color(0xFFF45C43)))
)
