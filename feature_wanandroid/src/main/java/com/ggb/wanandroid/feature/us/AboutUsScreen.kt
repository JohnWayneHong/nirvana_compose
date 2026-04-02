package com.ggb.wanandroid.feature.us

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ggb.wanandroid.R


/**
 * 关于我们
 * */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsScreen(
    modifier: Modifier = Modifier.fillMaxHeight().fillMaxWidth(),
    navController: NavController
){

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(stringResource(R.string.about_us_title), color = MaterialTheme.colorScheme.onPrimary, fontSize = 16.sp)
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            contentDescription = stringResource(R.string.common_back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarColors(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .nestedScroll(scrollBehavior.nestedScrollConnection)

        ) {

            item {
                Spacer(Modifier.size(12.dp))

                Text(
                    text = stringResource(R.string.about_us_app_name),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp
                )
            }

            item {
                Spacer(Modifier.size(20.dp))

                Text(
                    text = stringResource(R.string.about_us_guide_title),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp
                )
            }

            item {
                Spacer(Modifier.size(20.dp))

                Text(
                    text = stringResource(R.string.about_us_github),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp
                )
            }

        }
    }


}