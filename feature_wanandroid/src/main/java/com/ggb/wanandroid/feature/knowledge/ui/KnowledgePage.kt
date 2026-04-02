package com.ggb.wanandroid.feature.knowledge.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ggb.wanandroid.R
import com.ggb.wanandroid.feature.knowledge.KnowledgeViewModel
import com.ggb.wanandroid.navigation.navigateToKnowledgeDetail


@Composable
fun KnowledgePage(
    viewModel : KnowledgeViewModel = viewModel(),
    navController: NavController? = null,
    onToggleBars: (Boolean) -> Unit = {},
    nestedScrollConnection: NestedScrollConnection? = null
    ){

    val treeList by viewModel.treeList.collectAsState()
    val showLoadingDialog by viewModel.showLoadingDialog.collectAsState()
    val loadingMessage by viewModel.loadingMessage.collectAsState()


    val listState = rememberLazyListState()

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

    // Loading 对话框（菊花 loading + 自定义消息）
    if (showLoadingDialog) {

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
//        AlertDialog(
//            onDismissRequest = { /* 不允许点击外部关闭 */ },
//            title = null,
//            text = {
//                Column(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 24.dp, horizontal = 16.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    // 菊花 loading（CircularProgressIndicator）
//                    CircularProgressIndicator(
//                        modifier = Modifier.size(48.dp),
//                        color = androidx.compose.ui.res.colorResource(id = R.color.nav_selected),
//                        strokeWidth = 4.dp
//                    )
//                    // 自定义 loading 消息（如果有）
//                    if (loadingMessage.isNotEmpty()) {
//                        Spacer(modifier = Modifier.height(16.dp))
//                        Text(
//                            text = loadingMessage,
//                            fontSize = 14.sp,
//                            textAlign = TextAlign.Center,
//                            color = androidx.compose.ui.res.colorResource(id = R.color.nav_unselected)
//                        )
//                    }
//                }
//            },
//            confirmButton = {},
//            dismissButton = {},
//            properties = DialogProperties(
//                dismissOnBackPress = false,
//                dismissOnClickOutside = false
//            )
//        )
    }

    if (treeList.isEmpty()){
        // 空列表状态（只有在没有 header 时才显示）
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.common_no_data),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        }

    }else{
        KnowledgeList(
            modifier = Modifier.fillMaxSize(),
            treeList = treeList,
            listState = listState,
            nestedScrollConnection = nestedScrollConnection,
            onItemClick = { tree ->
                // 使用扩展函数导航到详情页（内部会处理缓存）
                navController?.navigateToKnowledgeDetail(tree)
            }
        )
    }
}