package com.ggb.wanandroid.feature.link

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.zfx.commonlib.ext.compose.Center
import com.zfx.commonlib.ext.compose.WebViewComposable
import com.ggb.wanandroid.R


@Composable
fun LinkScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    title : String,
    linkUrl : String,
    onBackClick: (() -> Unit)? = null,
    navController: NavController? = null,
    onToggleBars: (Boolean) -> Unit = {}
){
    Scaffold(
        modifier = modifier,
        topBar = {
            Box(
                modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary
                )){
                Row(
                    modifier = Modifier
                        .fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Spacer(
                        Modifier.size(12.dp)
                    )

                    Image(
                        modifier = Modifier
                            .height(24.dp)
                            .width(16.dp)
                            .clickable {
                                onBackClick?.invoke()
                                navController?.popBackStack() },
                        painter = painterResource(R.drawable.icon_back_white),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                        contentDescription = stringResource(R.string.link_back_button)
                    )

                    Spacer(
                        Modifier.weight(1f)
                    )

                    Image(
                        modifier = Modifier
                            .height(24.dp)
                            .width(24.dp)
                            .clickable {
                                //TODO:更多按钮pop
                            },
                        painter = painterResource(R.drawable.icon_more),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                        contentDescription = stringResource(R.string.link_more_button)
                    )

                    Spacer(
                        Modifier.size(12.dp)
                    )

                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 60.dp)
                ){
                    Center() {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            maxLines = 1,
                            modifier = Modifier.basicMarquee(),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }



        }
    ) {  paddingValues ->

        var url by remember { mutableStateOf(linkUrl) }

        WebViewComposable(
            url = url,
            modifier = Modifier.padding(paddingValues)
        )


    }
}