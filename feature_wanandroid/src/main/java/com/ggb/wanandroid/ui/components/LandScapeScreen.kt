package com.ggb.wanandroid.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemColors
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ggb.wanandroid.R
import com.ggb.wanandroid.feature.setting.SettingViewModel
import com.ggb.wanandroid.navigation.Routes
import com.ggb.wanandroid.navigation.navigateToHome
import com.ggb.wanandroid.navigation.navigateToKnowledge
import com.ggb.wanandroid.navigation.navigateToSquare
import com.ggb.wanandroid.navigation.navigateToWechat
import com.ggb.wanandroid.navigation.rememberNavState
import com.ggb.wanandroid.navigation.setupNavigation
import com.ggb.wanandroid.util.NavControllerManager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandScapeScreen(
    settingViewModel : SettingViewModel
){
    val navController = rememberNavController()

    // 注册 NavController 到全局管理器
    DisposableEffect(navController) {
        NavControllerManager.registerNavController(navController)
        onDispose {
            NavControllerManager.unregisterNavController()
        }
    }
    // 使用导航状态管理，统一管理导航相关状态
    val navState = rememberNavState(navController)

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // BottomBar 显隐状态
    val barsVisible = remember { mutableStateOf(true) }

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // BottomBar - 覆盖在内容上方（不占用布局空间）
        if (navState.showBottomBar) {
            NavigationRail(
                modifier = Modifier.width(56.dp),
                containerColor = MaterialTheme.colorScheme.tertiary
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    //首页
                    NavigationRailItem(
                        modifier = Modifier.weight(1f),
                        selected = navState.currentRoute == Routes.HOME,
                        onClick = {
                            navController.navigateToHome()
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.icon_menu_home_grey),
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(
                                stringResource(R.string.nav_home),
                                color = if (navState.currentRoute == Routes.HOME) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
//                    color = colorResource(
//                        id = if (currentRoute == Routes.HOME) {
//                            R.color.nav_selected
//                        } else {
//                            R.color.nav_unselected
//                        }
//                    )
                            )
                        },
                        colors = navigationBarItemColors()
                    )

                    //广场
                    NavigationRailItem(
                        modifier = Modifier.weight(1f),
                        selected = navState.currentRoute == Routes.SQUARE,
                        onClick = {
                            navController.navigateToSquare()
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.icon_menu_square),
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(
                                stringResource(R.string.nav_square),
                                color = if (navState.currentRoute == Routes.SQUARE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
//                    color = colorResource(
//                        id = if (currentRoute == Routes.SQUARE) {
//                            R.color.nav_selected
//                        } else {
//                            R.color.nav_unselected
//                        }
//                    )
                            )
                        },
                        colors = navigationBarItemColors()
                    )

                    //公众号
                    NavigationRailItem(
                        modifier = Modifier.weight(1f),
                        selected = navState.currentRoute == Routes.WECHAT,
                        onClick = {
                            navController.navigateToWechat()
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.icon_menu_official_accounts),
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(
                                stringResource(R.string.nav_wechat),
                                color = if (navState.currentRoute == Routes.WECHAT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
//                    color = colorResource(
//                        id = if (currentRoute == Routes.WECHAT) {
//                            R.color.nav_selected
//                        } else {
//                            R.color.nav_unselected
//                        }
//                    )
                            )
                        },
                        colors = navigationBarItemColors()
                    )

                    //体系
                    NavigationRailItem(
                        modifier = Modifier.weight(1f),
                        selected = navState.currentRoute == Routes.KNOWLEDGE,
                        onClick = {
                            navController.navigateToKnowledge()
                        },
                        icon = {
                            Icon(
                                painter = painterResource(id = R.drawable.icon_menu_knowledge_grey),
                                contentDescription = null
                            )
                        },
                        label = {
                            Text(
                                stringResource(R.string.nav_knowledge),
                                color = if (navState.currentRoute == Routes.KNOWLEDGE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
//                    color = colorResource(
//                        id = if (currentRoute == Routes.KNOWLEDGE) {
//                            R.color.nav_selected
//                        } else {
//                            R.color.nav_unselected
//                        }
//                    )
                            )
                        },
                        colors = navigationBarItemColors()
                    )
                }
            }



        }

        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
        ) {
            setupNavigation(
                navController = navController,
                onToggleBars = { show -> barsVisible.value = show },
                barsVisible = barsVisible,
                drawerState = drawerState,
                settingViewModel = settingViewModel
            )
        }
    }

}

/**
 * 获取导航栏 Item 的颜色配置
 */
@Composable
private fun navigationBarItemColors(): NavigationRailItemColors = NavigationRailItemColors(
    selectedIconColor = MaterialTheme.colorScheme.primary,
    selectedTextColor = MaterialTheme.colorScheme.primary,
    selectedIndicatorColor = Color.Transparent,
    unselectedIconColor = MaterialTheme.colorScheme.surface,
    unselectedTextColor = MaterialTheme.colorScheme.surface,
    disabledIconColor = MaterialTheme.colorScheme.surface,
    disabledTextColor = MaterialTheme.colorScheme.surface
)
