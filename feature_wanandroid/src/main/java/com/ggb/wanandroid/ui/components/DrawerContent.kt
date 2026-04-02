package com.ggb.wanandroid.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.ggb.wanandroid.R
import com.ggb.wanandroid.feature.coin.data.CoinData
import com.ggb.wanandroid.feature.coin.data.PersonalCoinData
import com.ggb.wanandroid.navigation.Routes
import com.ggb.wanandroid.navigation.navigateToAccount
import com.ggb.wanandroid.navigation.navigateToAboutUs
import com.ggb.wanandroid.navigation.navigateToCoinDetail
import com.ggb.wanandroid.navigation.navigateToCoinRank
import com.ggb.wanandroid.navigation.navigateToSetting
import com.ggb.wanandroid.util.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 抽屉内容
 */
@Composable
fun DrawerContent(
    navController: NavController,
    drawerState: androidx.compose.material3.DrawerState,
    modifier: Modifier = Modifier
) {
    // 使用 remember + mutableStateOf 来保存用户名和积分状态
    var userName by remember { mutableStateOf(UserPreferences.getUsername()) }
    var userCoin by remember {
        mutableStateOf(parsePersonalCoinData(UserPreferences.getPersonalCoin()))
    }
    var isLoggedIn by remember { mutableStateOf(UserPreferences.isLoggedIn()) }


    // 当 Drawer 打开时，重新读取用户名和积分（这样可以响应登录后的变化）
    LaunchedEffect(drawerState.isOpen) {
        if (drawerState.isOpen) {
            userName = UserPreferences.getUsername()
            userCoin = parsePersonalCoinData(UserPreferences.getPersonalCoin())
        }
    }

    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary
                )
                .padding(12.dp),
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    modifier = Modifier
                        .size(48.dp)
                        .then(
                            // 只有在未登录时才可点击
                            if (!isLoggedIn) {
                                Modifier.clickable {
                                    scope.launch {
                                        drawerState.close()
                                    }
                                    navController.navigateToAccount()
                                }
                            } else {
                                Modifier
                            }
                        ),
                    painter = painterResource(R.drawable.icon_avatar_default),
                    contentDescription = stringResource(R.string.drawer_user_avatar)
                )

                Spacer(Modifier.size(12.dp))

                Text(
                    text = userName?.takeIf { it.isNotEmpty() } ?: stringResource(R.string.drawer_please_login),
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 12.sp
                )

                Spacer(Modifier.size(12.dp))

                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.drawer_level_placeholder,userCoin?.level?.toString() ?: "-"),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp
                    )

                    Spacer(Modifier.size(12.dp))

                    Text(
                        text = stringResource(R.string.drawer_rank_placeholder,userCoin?.rank?.toString() ?: "-"),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp
                    )
                }
            }

            Icon(
                modifier = Modifier
                    .size(48.dp)
                    .offset(140.dp,12.dp)
                    .clickable {
                        scope.launch {
                            drawerState.close()
                        }
                        navController.navigateToCoinRank()
                    },
                painter = painterResource(R.drawable.icon_coin_rank),
                tint = MaterialTheme.colorScheme.onPrimary,
                contentDescription = stringResource(R.string.drawer_coin_rank_desc)
            )
        }

        DrawerItem(
            onItemClick = {
                if (isLoggedIn){
                    scope.launch {
                        drawerState.close()
                    }
                    navController.navigateToCoinDetail(userCoin?.coinCount?.toString() ?: "")
                }

            },
            imageRes = R.drawable.icon_coin,
            name = stringResource(R.string.drawer_my_coin),
            coin = userCoin?.coinCount?.toString()
        )

        DrawerItem(
            onItemClick = {
                if (isLoggedIn){
                    scope.launch {
                        drawerState.close()
                    }
                }
            },
            imageRes = R.drawable.icon_favorite,
            name = stringResource(R.string.drawer_my_favorites)
        )

        Spacer(Modifier.size(2.dp))

        DrawerItem(
            onItemClick = {
                scope.launch {
                    drawerState.close()
                }
                navController.navigateToSetting()
            },
            imageRes = R.drawable.icon_setting,
            name = stringResource(R.string.drawer_system_settings)
        )

        Spacer(Modifier.size(2.dp))

        DrawerItem(
            onItemClick = {
                scope.launch {
                    drawerState.close()
                }
                navController.navigateToAboutUs()
            },
            imageRes = R.drawable.icon_about_us,
            name = stringResource(R.string.drawer_about_us)
        )



    }
}

@Composable
fun DrawerItem(
    onItemClick : () -> Unit,
    imageRes : Int,
    name : String,
    coin : String? = null
){

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .height(40.dp)
            .clickable{
                onItemClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            modifier = Modifier.size(18.dp),
            painter = painterResource(imageRes),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
            contentDescription = name
        )

        Spacer(Modifier.size(12.dp))

        Text(
            text = name,
            color = MaterialTheme.colorScheme.surface,
            fontSize = 12.sp
        )

        if (coin != null){
            Spacer(Modifier.weight(1f))
            Text(
                text = coin,
                color = MaterialTheme.colorScheme.surface,
                fontSize = 14.sp
            )
        }
    }

}

/**
 * 从 JSON 解析用户积分数据，失败或空时返回 null
 */
private fun parsePersonalCoinData(json: String?): CoinData? {
    if (json.isNullOrBlank()) return null
    return try {
        Gson().fromJson(json, CoinData::class.java)
    } catch (e: Exception) {
        null
    }
}


