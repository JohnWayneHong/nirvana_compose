package com.ggb.wanandroid.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscoverScreen(onEnterWanAndroid: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("发现", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.Search, contentDescription = "搜索")
                    }
                    IconButton(onClick = { /* TODO */ }) {
                        Icon(Icons.Default.QrCodeScanner, contentDescription = "扫码")
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
                .background(MaterialTheme.colorScheme.background)
        ) {
            // 1. 顶部入口金刚位
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    ShortcutItem(Icons.Default.Android, "玩安卓", Color(0xFF4CAF50)) { onEnterWanAndroid() }
                    ShortcutItem(Icons.Default.Build, "工具箱", Color(0xFF2196F3)) { }
                    ShortcutItem(Icons.Default.EmojiEvents, "排行榜", Color(0xFFFF9800)) { }
                    ShortcutItem(Icons.Default.Lightbulb, "每日一问", Color(0xFF9C27B0)) { }
                }
            }

            // 2. 热门活动推广位
            item {
                PromotionCard()
            }

            // 3. 发现流标题
            item {
                Text(
                    "为你推荐",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // 4. 模拟推荐列表
            items(getMockDiscoverData()) { item ->
                DiscoverListItem(item)
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun ShortcutItem(icon: ImageVector, label: String, bgColor: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .background(bgColor.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = bgColor, modifier = Modifier.size(26.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun PromotionCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF6200EE), Color(0xFFBB86FC))
                    )
                )
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Column {
                Text("2024 年度开发者大会", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("查看议程安排与精彩回放 >", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun DiscoverListItem(data: DiscoverData) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = data.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(data.tag, color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, 
                    modifier = Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(data.author, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("· ${data.time}", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
            }
        }
        
        if (data.hasImage) {
            Spacer(modifier = Modifier.width(12.dp))
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Image, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

data class DiscoverData(
    val title: String,
    val author: String,
    val tag: String,
    val time: String,
    val hasImage: Boolean = false
)

private fun getMockDiscoverData() = listOf(
    DiscoverData("Kotlin Multiplatform 现已进入稳定阶段，你会尝试吗？", "JetBrains", "官方新闻", "2小时前", true),
    DiscoverData("Jetpack Compose 动画全解：从简单淡入到复杂手势联动", "Android开发者", "技术方案", "5小时前"),
    DiscoverData("深入理解协程作用域：SupervisorJob 与 Job 的区别", "码农小博", "干货分享", "1天前"),
    DiscoverData("WanAndroid 社区精品文章征集令：丰厚积分等你来领", "管理员", "活动公告", "2天前", true),
    DiscoverData("Flutter vs Compose：2024 年移动端跨平台技术选型建议", "跨平台架构", "行业趋势", "3天前")
)
