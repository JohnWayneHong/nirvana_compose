package com.ggb.wanandroid.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ggb.wanandroid.feature.setting.SettingViewModel
import com.ggb.wanandroid.feature.setting.SettingViewModelFactory
import com.ggb.wanandroid.feature.setting.displayName
import com.ggb.wanandroid.feature.setting.ui.LanguageSelectDialog
import com.ggb.wanandroid.feature.setting.ui.ThemeSelectDialog
import com.ggb.wanandroid.main.R
import com.ggb.wanandroid.ui.theme.themeName

@Composable
fun MeScreen() {
    val context = LocalContext.current
    val settingViewModel: SettingViewModel = viewModel(
        factory = SettingViewModelFactory(context)
    )
    val curTheme by settingViewModel.curTheme.collectAsState()
    val curLanguage by settingViewModel.curLanguage.collectAsState()
    
    val isDark = isSystemInDarkTheme()
    val bgColor = if (isDark) Color(0xFF121212) else Color(0xFFF8F9FA) 
    
    var showThemeDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .verticalScroll(scrollState)
    ) {
        // 1. 头部布局
        HeaderSection()

        // 2. 统计卡片
        StatsCard()

        Spacer(modifier = Modifier.height(24.dp))

        // 3. 我的成就
        SectionTitle(stringResource(R.string.me_my_achievement))
        MenuCard {
            ModernMenuItem(
                icon = Icons.Default.Favorite,
                iconBg = Color(0xFFFFF1F1),
                iconColor = Color(0xFFFF5252),
                title = stringResource(R.string.me_my_favorites),
                value = "12"
            )
            ModernMenuItem(
                icon = Icons.AutoMirrored.Filled.Article,
                iconBg = Color(0xFFE8F4FD),
                iconColor = Color(0xFF2196F3),
                title = stringResource(R.string.me_my_articles),
                value = "0"
            )
            ModernMenuItem(
                icon = Icons.Default.History,
                iconBg = Color(0xFFF0F7ED),
                iconColor = Color(0xFF4CAF50),
                title = stringResource(R.string.me_browse_history),
                value = "88"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 4. 系统服务
        SectionTitle(stringResource(R.string.me_system_service))
        MenuCard {
            ModernMenuItem(
                icon = Icons.Default.Palette,
                iconBg = Color(0xFFFFF8E1),
                iconColor = Color(0xFFFFC107),
                title = stringResource(R.string.me_appearance_setting),
                value = curTheme.themeName(),
                onClick = { showThemeDialog = true }
            )
            ModernMenuItem(
                icon = Icons.Default.Language,
                iconBg = Color(0xFFE0F2F1),
                iconColor = Color(0xFF009688),
                title = stringResource(R.string.me_language_setting),
                value = curLanguage.displayName(),
                onClick = { showLanguageDialog = true }
            )
            ModernMenuItem(
                icon = Icons.Default.Info,
                iconBg = Color(0xFFF3E5F5),
                iconColor = Color(0xFF9C27B0),
                title = stringResource(R.string.me_about_project)
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
            Text(stringResource(R.string.me_logout), fontWeight = FontWeight.Medium)
        }
        
        Spacer(modifier = Modifier.height(40.dp))
    }

    if (showThemeDialog) {
        ThemeSelectDialog(
            onSelect = { theme ->
                settingViewModel.saveTheme(theme)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }

    if (showLanguageDialog) {
        LanguageSelectDialog(
            currentLanguage = curLanguage,
            onLanguageSelect = { language ->
                settingViewModel.saveLanguage(language)
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
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
            modifier = Modifier.align(Alignment.TopEnd).padding(top = 40.dp, end = 16.dp)
        ) {
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Outlined.Notifications, "消息", tint = Color.White)
            }
            IconButton(onClick = { /* TODO */ }) {
                Icon(Icons.Outlined.Settings, "设置", tint = Color.White)
            }
        }

        Column(
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 24.dp, bottom = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(72.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .padding(4.dp).clip(CircleShape).background(Color.White)
                ) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.fillMaxSize().padding(12.dp), tint = primary)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(stringResource(R.string.me_click_login), style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
                    Text(stringResource(R.string.me_unlock_more), style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.8f))
                }
            }
        }
    }
}

@Composable
private fun StatsCard() {
    val isDark = isSystemInDarkTheme()
    val cardColor = if (isDark) Color(0xFF1E1E1E) else Color.White
    Card(
        modifier = Modifier.padding(horizontal = 24.dp).offset(y = (-30).dp)
            .shadow(12.dp, RoundedCornerShape(20.dp), ambientColor = if (isDark) Color.Transparent else Color.Black.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            StatItem(stringResource(R.string.me_my_coin), "1,250")
            VerticalDivider(modifier = Modifier.height(30.dp).align(Alignment.CenterVertically), color = Color.LightGray.copy(alpha = 0.5f))
            StatItem(stringResource(R.string.me_following), "12")
            VerticalDivider(modifier = Modifier.height(30.dp).align(Alignment.CenterVertically), color = Color.LightGray.copy(alpha = 0.5f))
            StatItem(stringResource(R.string.me_level), "Lv.5")
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(start = 28.dp, bottom = 10.dp),
        style = MaterialTheme.typography.titleSmall.copy(
            color = MaterialTheme.colorScheme.primary, 
            fontWeight = FontWeight.Bold
        )
    )
}

@Composable
private fun MenuCard(content: @Composable ColumnScope.() -> Unit) {
    val isDark = isSystemInDarkTheme()
    val cardColor = if (isDark) Color(0xFF1E1E1E) else Color.White
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp), ambientColor = if (isDark) Color.Transparent else Color.Black.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(vertical = 8.dp), content = content)
    }
}

@Composable
private fun ModernMenuItem(icon: ImageVector, iconBg: Color, iconColor: Color, title: String, value: String = "", onClick: () -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(40.dp).background(iconBg, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
        if (value.isNotEmpty()) {
            Text(text = value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(4.dp))
        }
        Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.sp
            )
        )
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
