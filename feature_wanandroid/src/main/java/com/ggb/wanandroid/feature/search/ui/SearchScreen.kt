package com.ggb.wanandroid.feature.search.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.drawBehind
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ggb.wanandroid.R
import com.ggb.wanandroid.data.Article
import com.ggb.wanandroid.feature.home.ui.ArticleItem
import com.ggb.wanandroid.feature.search.SearchViewModel
import com.ggb.wanandroid.navigation.Routes
import com.blankj.utilcode.util.ToastUtils
import com.ggb.wanandroid.data.HotKey

/**
 * Material3 搜索页面
 * 
 * 使用 SearchBar 组件实现完整的搜索功能：
 * - 搜索栏（支持展开/收起）
 * - 搜索历史
 * - 搜索结果列表
 * - 热门搜索推荐
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = viewModel()
) {
    val searchResults by viewModel.searchResults.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState()
    val hotSearch by viewModel.hotSearch.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    var query by remember { mutableStateOf("") }
    val showResults = query.isNotBlank() && searchResults.isNotEmpty()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary)
    ) {
        // 自定义 SearchBar
        CustomSearchBar(
            query = query,
            onQueryChange = { newQuery ->
                query = newQuery
                if (newQuery.isNotBlank()) {
                    viewModel.search(newQuery)
                }
            },
            onSearch = { searchQuery ->
                if (searchQuery.isNotBlank()) {
                    viewModel.search(searchQuery)
                    viewModel.addToHistory(searchQuery)
                    focusManager.clearFocus()
                }
            },
            onBackClick = { navController.popBackStack() },
            onClearClick = { query = "" }
        )

        // 搜索结果列表
        if (showResults) {
            SearchResultsList(
                results = searchResults,
                isLoading = isLoading,
                onArticleClick = { article ->
                    navController?.navigate(
                        Routes.buildLinkRoute(article.title, article.link)
                    )
                }
            )
        } else if (query.isBlank()) {
            // 默认显示：搜索建议（历史 + 热门）
            SearchSuggestions(
                searchHistory = searchHistory,
                hotSearch = hotSearch,
                onHistoryClick = { history ->
                    query = history
                    viewModel.search(history)
                },
                onHotSearchClick = { hot ->
                    query = hot.name
                    viewModel.search(hot.name)
                    viewModel.addToHistory(hot.name)
                },
                onClearHistory = { viewModel.clearHistory() }
            )
        }
    }
}

/**
 * 自定义搜索栏：返回键、输入框、清除按钮，支持键盘搜索
 */
@Composable
fun CustomSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onBackClick: () -> Unit,
    onClearClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(MaterialTheme.colorScheme.primary),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(Modifier.size(12.dp))
        Image(
            modifier = Modifier
                .height(24.dp)
                .width(16.dp)
                .clickable(onClick = onBackClick),
            painter = painterResource(R.drawable.icon_back_white),
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
            contentDescription = stringResource(R.string.search_back_button)
        )
        Spacer(Modifier.size(24.dp))
        Row(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = stringResource(R.string.search_placeholder),
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimary)
                )
            }
            if (query.isNotBlank()) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.search_clear),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(onClick = onClearClick)
                )
            }
        }
        Spacer(Modifier.size(16.dp))
    }
}

/**
 * 搜索建议内容：未输入关键词时在搜索栏下方显示历史与热门
 */
@Composable
fun SearchSuggestions(
    searchHistory: List<String>,
    hotSearch: List<HotKey>,
    onHistoryClick: (String) -> Unit,
    onHotSearchClick: (HotKey) -> Unit,
    onClearHistory: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 搜索历史
        if (searchHistory.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.search_history),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = onClearHistory) {
                        Text(stringResource(R.string.search_clear_history), fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
            
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    searchHistory.forEach { history ->
                        SearchChip(
                            text = history,
                            onClick = { onHistoryClick(history) }
                        )
                    }
                }
            }
        }
        
        // 热门搜索
        if (hotSearch.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.search_hot),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Image(
                        painter = painterResource(R.drawable.icon_hot_white),
                        modifier = Modifier.size(14.dp), // 设置图标大小
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.error),
                        contentDescription = stringResource(R.string.search_hot_icon)
                    )
                }
            }
            
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    hotSearch.forEach { hot ->
                        SearchChip(
                            text = hot.name,
                            onClick = { onHotSearchClick(hot) },
                            isHot = true
                        )
                    }
                }
            }
        }
    }
}

/**
 * 默认搜索内容（未搜索时显示）
 */
@Composable
fun DefaultSearchContent(
    searchHistory: List<String>,
    hotSearch: List<HotKey>,
    onHistoryClick: (String) -> Unit,
    onHotSearchClick: (HotKey) -> Unit,
    onClearHistory: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 搜索历史
        if (searchHistory.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.search_history),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.surface
                    )
                    TextButton(onClick = onClearHistory) {
                        Text(stringResource(R.string.search_clear_history), fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
            
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    searchHistory.forEach { history ->
                        SearchChip(
                            text = history,
                            onClick = { onHistoryClick(history) }
                        )
                    }
                }
            }
        }
        
        // 热门搜索
        if (hotSearch.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.search_hot),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.surface
                )
            }
            
            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    hotSearch.forEach { hot ->
                        SearchChip(
                            text = hot.name,
                            onClick = { onHotSearchClick(hot) },
                            isHot = true
                        )
                    }
                }
            }
        }
    }
}

/**
 * 搜索结果列表
 */
@Composable
fun SearchResultsList(
    results: List<Article>,
    isLoading: Boolean,
    onArticleClick: (Article) -> Unit
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
        }
    } else if (results.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.search_no_results),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(
                items = results,
                key = { article -> "${article.id}_${article.publishTime}" }
            ) { article ->
                val articleToast = stringResource(R.string.article_favorite, article.title)
                ArticleItem(
                    data = article,
                    favoriteClick = {
                        ToastUtils.showShort(articleToast)
                    },
                    cardClick = { onArticleClick(article) }
                )
            }
        }
    }
}

/**
 * 搜索标签（Chip）
 */
@Composable
fun SearchChip(
    text: String,
    onClick: () -> Unit,
    isHot: Boolean = false
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isHot) {
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        } else {
            MaterialTheme.colorScheme.tertiary
        },
        modifier = Modifier.padding(0.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 10.sp,
            color = if (isHot) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    }
}

