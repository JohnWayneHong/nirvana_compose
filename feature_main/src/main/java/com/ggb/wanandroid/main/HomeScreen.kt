package com.ggb.wanandroid.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    // 缓存模拟数据，避免重组时重新创建列表导致卡顿
    val articleList = remember { getMockHomeData() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp)
                            .height(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable { /* TODO: Search */ }
                            .padding(horizontal = 12.dp)
                    ) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "搜索关键词...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.NotificationsNone, contentDescription = "通知")
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

            item(key = "featured_columns_header") { SectionHeader("精选专栏", "查看全部") }

            item(key = "featured_columns_row") { FeaturedColumns() }

            item(key = "recent_updates_header") { SectionHeader("最近更新", "") }

            // 传入缓存的列表
            items(
                items = articleList,
                key = { it.id }
            ) { item ->
                HomeArticleItem(item)
            }
        }
    }
}

@Composable
private fun HomeBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(160.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF3A7BD5), Color(0xFF00D2FF))
                    )
                )
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
                        "HOT",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Jetpack Compose 性能优化实战技巧",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
private fun HomeQuickActions() {
    val actions = remember {
        listOf(
            ActionItem(Icons.AutoMirrored.Filled.LibraryBooks, "教程", Color(0xFF4CAF50)),
            ActionItem(Icons.Default.Code, "项目", Color(0xFF2196F3)),
            ActionItem(Icons.Default.QuestionAnswer, "问答", Color(0xFFFF9800)),
            ActionItem(Icons.Default.EmojiEvents, "竞赛", Color(0xFFE91E63))
        )
    }

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
    val columns = remember { listOf("Compose实战", "KMP跨平台", "架构组件", "性能调优") }
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(columns) { column ->
            Surface(
                // 使用比背景稍亮的颜色
                color = if (isSystemInDarkTheme()) Color(0xFF373737) else Color(0xFFF5F7F9),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.width(130.dp).clickable { }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Icon(Icons.Default.Folder, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(column, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("12 篇文章", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun HomeArticleItem(item: HomeData) {
    // 显式指定颜色：如果是浅色模式，使用纯白；深色模式使用较浅的灰色
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

private fun getMockHomeData() = listOf(
    HomeData(1, "Compose 自定义布局详解：从测量到放置", "鸿洋", "20分钟前", listOf("Compose", "自定义")),
    HomeData(2, "Android 15 适配指南：开发者需要关注的重大变更", "Google", "1小时前", listOf("Android 15"), true),
    HomeData(3, "Kotlin 2.0 K2 编译器正式版发布", "JetBrains", "3小时前", listOf("Kotlin")),
    HomeData(4, "使用 Room 和 Hilt 构建现代架构", "郭霖", "昨天", listOf("架构", "数据库"), true),
    HomeData(5, "如何在 iOS 上运行 Compose 代码", "Nirvana", "2天前", listOf("KMP", "iOS"))
)