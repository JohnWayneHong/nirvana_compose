package com.ggb.wanandroid.feature.navigation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ggb.wanandroid.R
import com.ggb.wanandroid.data.NavigationJson
import com.ggb.wanandroid.feature.navigation.NavigationViewModel
import com.ggb.wanandroid.navigation.Routes
import com.ggb.wanandroid.ui.components.VerticalScrollableTabRow
import kotlinx.coroutines.launch
import kotlin.random.Random


@Composable
fun NavigationScreen(
    viewModel: NavigationViewModel = viewModel(),
    navController: NavController? = null,
    onToggleBars: (Boolean) -> Unit = {},
    nestedScrollConnection: NestedScrollConnection? = null
){

    val navigationJson by viewModel.navigationJson.collectAsState()
    val showLoading by viewModel.showLoading.collectAsState()

    val tabData = remember(navigationJson) {
        navigationJson.map {
            it.name
        }
    }

    val listState = rememberLazyListState()
    var selectedIndex by remember { mutableStateOf(0) }
    var tabClicking by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { index ->
                if (!tabClicking && selectedIndex != index){
                    selectedIndex = index
                }
            }
    }

    LaunchedEffect(listState) {
        var last = 0
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (index, offset) ->
                val current = index * 100000 + offset
                val delta = current - last
                if (kotlin.math.abs(delta) > 6) {
                    onToggleBars(delta < 0)
                    last = current
                }
                if (index == 0 && offset == 0) {
                    onToggleBars(true)
                }
            }
    }
    if (showLoading){
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )
        }
    }

    if (navigationJson.isNotEmpty()){
        Row(
            modifier = Modifier.fillMaxSize()
        ) {

            VerticalScrollableTabRow(
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight(),
                tabs = tabData as List<String>,
                onTabSelected = { index ->
                    selectedIndex = index
                    tabClicking = true
                    scope.launch {
                        listState.animateScrollToItem(index)
                        tabClicking = false
                    }
                },
                selectedTabIndex = selectedIndex
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .background(color = MaterialTheme.colorScheme.onPrimary)
                    .then(
                        nestedScrollConnection?.let {
                            Modifier.nestedScroll(it)
                        } ?: Modifier
                    ),
                state = listState
            ) {
                items(
                    items = navigationJson,
                    key = { navigation -> navigation.cid } // 使用唯一 ID 作为 key，提升性能
                ) { navigation ->
                    NavigationItem(navigation,navController)
                }

            }
        }
    }
}

@Composable
fun NavigationItem(item: NavigationJson,navController: NavController? = null) {
    val textColors = remember {
        listOf(
            Color(0xff8a8a8a),
            Color(0xffd81e06),
            Color(0xfff4ea2a),
            Color(0xff1296db),
            Color(0xff1afa29),
            Color(0xff13227a),
            Color(0xffd4237a)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = item.name ?: "",
            color = MaterialTheme.colorScheme.surface,
            fontSize = 18.sp
        )

        FlowRow(
            verticalArrangement = Arrangement.spacedBy(8.dp),  // 竖向间距 8dp
            horizontalArrangement = Arrangement.spacedBy(8.dp),  // 横向间距 8dp
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            item.articles?.forEach { article ->
                val randomNumber = Random.nextInt(7)
                Text(
                    text = article.title,
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = textColors[randomNumber],
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 6.dp)
                        .clickable{
                            navController?.navigate(Routes.buildLinkRoute(article.title, article.link))
                        },
                    color = textColors[randomNumber],
                    fontSize = 16.sp
                )

            }
        }
    }
}