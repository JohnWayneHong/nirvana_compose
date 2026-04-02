package com.ggb.wanandroid.feature.coin.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import com.ggb.wanandroid.feature.coin.data.PersonalCoinData
import com.ggb.wanandroid.ui.components.RefreshableLazyList


@Composable
fun CoinListScreen(
    coinCount : String,
    navController: NavController,
    viewModel: CoinViewModel = viewModel()
){
    LaunchedEffect(Unit) {
        viewModel.refresh(true)
    }

    val coinList by viewModel.personalCoinList.collectAsState()
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
                        .width(12.dp)
                        .height(24.dp)
                        .clickable {
                            navController.popBackStack()
                        },
                    painter = painterResource(R.drawable.icon_back_white),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                    contentDescription = stringResource(R.string.settings_back_button)
                )

                Spacer(
                    Modifier.size(24.dp)
                )

                Text(
                    text = "得分详情",
                    fontSize = 18.sp,
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
            header = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .background(color = MaterialTheme.colorScheme.primary),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        coinCount,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 22.sp
                    )
                }
            },
            onRefresh = {
                viewModel.refresh(true)
            },
            onLoadMore = {
                viewModel.loadMore(true)
            },
            itemKey = { coin -> "${coin.id}_${coin.date}" },
        ) { coinData ->
            PersonalCoinListItem(coinData)
        }

    }
}

@Composable
fun PersonalCoinListItem(data: PersonalCoinData) {
    Column(
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    data.reason,
                    color = MaterialTheme.colorScheme.onTertiary,
                    fontSize = 10.sp
                )

                Text(
                    data.desc,
                    color = MaterialTheme.colorScheme.onTertiary,
                    fontSize = 8.sp
                )
            }


            Text(
                data.coinCount.toString(),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp
            )

        }

        HorizontalDivider(
            modifier = Modifier.height(1.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}