package com.ggb.wanandroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ggb.wanandroid.R
import kotlinx.coroutines.launch

/**
 * 垂直滚动的 TabRow
 * 用于替代 PrimaryScrollableTabRow 实现垂直滚动
 * 
 * @param selectedTabIndex 当前选中的 Tab 索引
 * @param tabs Tab 标题列表
 * @param onTabSelected Tab 选中回调
 * @param modifier 修饰符
 * @param containerColor 容器背景色
 * @param selectedTabColor 选中 Tab 的颜色
 * @param unselectedTabColor 未选中 Tab 的颜色
 * @param indicatorColor 指示器颜色
 */
@Composable
fun VerticalScrollableTabRow(
    selectedTabIndex: Int,
    tabs: List<String>,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.background,
    selectedTabColor: Color = MaterialTheme.colorScheme.primary,
    unselectedTabColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
    indicatorColor: Color = MaterialTheme.colorScheme.primary
) {
    val listState = rememberLazyListState()
    
    // 当选中 Tab 变化时，自动滚动到该 Tab
    LaunchedEffect(selectedTabIndex) {
        if (selectedTabIndex < tabs.size) {
            listState.animateScrollToItem(selectedTabIndex)
        }
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(containerColor)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxHeight()
        ) {
            itemsIndexed(tabs) { index, title ->
                val isSelected = index == selectedTabIndex
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTabSelected(index) }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 指示器（左侧）
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(20.dp)
                                    .background(
                                        color = indicatorColor,
                                        shape = RoundedCornerShape(2.dp)
                                    )
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(20.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Tab 文本
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) selectedTabColor else unselectedTabColor,
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 垂直滚动的 TabRow（带背景和边框的选中样式）
 */
@Composable
fun VerticalScrollableTabRowWithBackground(
    selectedTabIndex: Int,
    tabs: List<String>,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = colorResource(R.color.theme),
    selectedTabColor: Color = colorResource(R.color.white),
    unselectedTabColor: Color = colorResource(R.color.white).copy(alpha = 0.7f),
    selectedBackgroundColor: Color = colorResource(R.color.white).copy(alpha = 0.2f),
    selectedBorderColor: Color = colorResource(R.color.white)
) {
    val listState = rememberLazyListState()
    
    LaunchedEffect(selectedTabIndex) {
        if (selectedTabIndex < tabs.size) {
            listState.animateScrollToItem(selectedTabIndex)
        }
    }

    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(containerColor)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxHeight()
        ) {
            itemsIndexed(tabs) { index, title ->
                val isSelected = index == selectedTabIndex
                
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTabSelected(index) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .then(
                            if (isSelected) {
                                Modifier
                                    .background(
                                        color = selectedBackgroundColor,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = selectedBorderColor,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                            } else {
                                Modifier
                            }
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) selectedTabColor else unselectedTabColor
                    )
                }
            }
        }
    }
}

