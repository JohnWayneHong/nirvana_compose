package com.ggb.wanandroid.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ggb.wanandroid.R
import com.ggb.wanandroid.navigation.Routes
import com.ggb.wanandroid.navigation.navigateToHome
import com.ggb.wanandroid.navigation.navigateToKnowledge
import com.ggb.wanandroid.navigation.navigateToProgram
import com.ggb.wanandroid.navigation.navigateToSquare
import com.ggb.wanandroid.navigation.navigateToWechat

/**
 * 底部导航栏
 */
@Composable
fun BottomNavigationBar(
    navController: NavController,
    currentRoute: String?,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.height(56.dp),
        containerColor = MaterialTheme.colorScheme.tertiary
    ) {
        //首页
        NavigationBarItem(
            selected = currentRoute == Routes.HOME,
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
                    color = if (currentRoute == Routes.HOME) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
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
        NavigationBarItem(
            selected = currentRoute == Routes.SQUARE,
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
                    color = if (currentRoute == Routes.SQUARE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
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
        NavigationBarItem(
            selected = currentRoute == Routes.WECHAT,
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
                    color = if (currentRoute == Routes.WECHAT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
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
        NavigationBarItem(
            selected = currentRoute == Routes.KNOWLEDGE,
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
                    color = if (currentRoute == Routes.KNOWLEDGE) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
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

        //项目
        NavigationBarItem(
            selected = currentRoute == Routes.PROGRAM,
            onClick = {
                navController.navigateToProgram()
            },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.icon_menu_program),
                    contentDescription = null
                )
            },
            label = {
                Text(
                    stringResource(R.string.nav_program),
                    color = if (currentRoute == Routes.PROGRAM) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                )
            },
            colors = navigationBarItemColors()
        )
    }
}


/**
 * 获取导航栏 Item 的颜色配置
 */
@Composable
private fun navigationBarItemColors(): NavigationBarItemColors = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.primary,
    selectedTextColor = MaterialTheme.colorScheme.primary,
    indicatorColor = Color.Transparent,
    unselectedIconColor = MaterialTheme.colorScheme.surface,
    unselectedTextColor = MaterialTheme.colorScheme.surface
)





