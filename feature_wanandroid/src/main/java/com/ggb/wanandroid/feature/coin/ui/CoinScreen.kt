package com.ggb.wanandroid.feature.coin.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ggb.wanandroid.R
import com.ggb.wanandroid.feature.coin.CoinViewModel
import com.ggb.wanandroid.feature.coin.data.CoinData
import com.ggb.wanandroid.ui.components.RefreshableLazyList


@Composable
fun CoinScreen(
    navController: NavController,
    viewModel: CoinViewModel = viewModel()
){
    // 使用 Unit 作为 key，进入页面时执行一次；用 viewModel 做 key 可能因引用未变而不触发
    LaunchedEffect(Unit) {
        viewModel.refresh(false)
    }

    val coinList by viewModel.coinList.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary
                    ),
                verticalAlignment = Alignment.CenterVertically
            ){
                Spacer(
                    Modifier.size(12.dp)
                )

                Image(
                    modifier = Modifier
                        .width(16.dp)
                        .height(24.dp)
                        .clickable {
                            navController.popBackStack()
                        },
                    painter = painterResource(R.drawable.icon_back_white),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                    contentDescription = stringResource(R.string.settings_back_button)
                )

                Spacer(
                    Modifier.size(12.dp)
                )

                Text(
                    text = stringResource(R.string.coin_rank_title),
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        },

    ) { paddingValues ->
        RefreshableLazyList(
            modifier = Modifier
                .padding(paddingValues)
                .background(color = MaterialTheme.colorScheme.background),
            items = coinList,
            isLoading = isLoading,
            isRefreshing = isRefreshing,
            hasMore = hasMore,
            onRefresh = {
                viewModel.refresh(false)
            },
            onLoadMore = {
                viewModel.loadMore(false)
            },
            itemKey = { coin -> "${coin.rank}_${coin.userId}" },
        ) { coinData ->
            val index = coinList.indexOf(coinData)
            CoinListItem(coinData,index == coinList.size -1)
        }
    }
}


@Composable
fun CoinListItem(coin : CoinData,isLast : Boolean){
    Column(
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .height(40.dp)
                .padding(horizontal = 12.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                coin.rank,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 12.sp
            )

            Spacer(
                Modifier.size(24.dp)
            )
            Text(
                coin.username,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 12.sp
            )

            Spacer(
                Modifier.weight(1f)
            )

            Text(
                coin.coinCount.toString(),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp
            )

        }

        if (!isLast){
            HorizontalDivider(
                modifier = Modifier.height(1.dp),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }

}