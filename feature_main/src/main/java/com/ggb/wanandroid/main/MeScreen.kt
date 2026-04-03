package com.ggb.wanandroid.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ggb.wanandroid.feature.setting.SettingViewModel
import com.ggb.wanandroid.feature.setting.SettingViewModelFactory
import com.ggb.wanandroid.ui.theme.AppTheme

@Composable
fun MeScreen() {
    val context = LocalContext.current
    val settingViewModel: SettingViewModel = viewModel(
        factory = SettingViewModelFactory(context)
    )
    val curTheme by settingViewModel.curTheme.collectAsState()
    
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        // 1. 头部布局
        HeaderSection()

        // 2. 统计卡片
        StatsCard()

        Spacer(modifier = Modifier.height(24.dp))

        // 3. 我的成就
        SectionTitle("我的成就")
        MenuCard {
            ModernMenuItem(
                icon = Icons.Default.Favorite,
                iconBg = Color(0xFFFFF1F1),
                iconColor = Color(0xFFFF5252),
                title = "我的收藏",
                value = "12"
            )
            ModernMenuItem(
                icon = Icons.AutoMirrored.Filled.Article,
                iconBg = Color(0xFFE8F4FD),
                iconColor = Color(0xFF2196F3),
                title = "我的文章",
                value = "0"
            )
            ModernMenuItem(
                icon = Icons.Default.History,
                iconBg = Color(0xFFF0F7ED),
                iconColor = Color(0xFF4CAF50),
                title = "浏览历史",
                value = "88"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. 系统服务
        SectionTitle("系统服务")
        MenuCard {
            ModernMenuItem(
                icon = Icons.Default.Palette,
                iconBg = Color(0xFFFFF8E1),
                iconColor = Color(0xFFFFC107),
                title = "外观设置",
                value = if (curTheme == AppTheme.AUTO) "跟随系统" else "已自定义"
            )
            ModernMenuItem(
                icon = Icons.Default.Language,
                iconBg = Color(0xFFE0F2F1),
                iconColor = Color(0xFF009688),
                title = "多语言设置",
                value = "中文"
            )
            ModernMenuItem(
                icon = Icons.Default.Info,
                iconBg = Color(0xFFF3E5F5),
                iconColor = Color(0xFF9C27B0),
                title = "关于项目"
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 5. 退出按钮
        OutlinedButton(
            onClick = { /* TODO */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("退出当前账号", fontWeight = FontWeight.Medium)
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun HeaderSection() {
    val primary = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(primary, primary.copy(alpha = 0.8f))
                )
            )
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 40.dp, end = 16.dp)
        ) {
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Outlined.Notifications, "消息", tint = Color.White)
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Outlined.Settings, "设置", tint = Color.White)
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 24.dp, bottom = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().padding(12.dp),
                        tint = primary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        "点击登录",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "解锁更多精彩内容",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsCard() {
    Card(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .offset(y = (-30).dp)
            .shadow(12.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White) // 强制使用白色增加对比度
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("我的积分", "1,250")
            VerticalDivider(modifier = Modifier.height(30.dp).align(Alignment.CenterVertically), color = Color.LightGray.copy(alpha = 0.5f))
            StatItem("关注", "12")
            VerticalDivider(modifier = Modifier.height(30.dp).align(Alignment.CenterVertically), color = Color.LightGray.copy(alpha = 0.5f))
            StatItem("等级", "Lv.5")
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(start = 28.dp, bottom = 10.dp),
        style = MaterialTheme.typography.titleSmall.copy(
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
private fun MenuCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White) // 强制白色
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp), content = content)
    }
}

@Composable
private fun ModernMenuItem(
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    title: String,
    value: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconBg, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium, color = Color(0xFF333333)) // 深色文字提高易读性
        )

        if (value.isNotEmpty()) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.width(4.dp))
        }

        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.sp,
                color = Color(0xFF1A1A1A)
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )
    }
}
