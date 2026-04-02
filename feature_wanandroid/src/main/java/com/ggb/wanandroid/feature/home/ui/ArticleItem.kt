package com.ggb.wanandroid.feature.home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ggb.wanandroid.R
import com.ggb.wanandroid.data.Article
import com.ggb.wanandroid.ui.theme.ColorBlack
import com.ggb.wanandroid.ui.theme.ColorFF8A8A8A

@Composable
fun ArticleItem(
    modifier: Modifier = Modifier.fillMaxWidth(),
    data : Article,
    favoriteClick : () -> Unit,
    cardClick : () -> Unit
){

    Card(
        modifier = modifier
            .wrapContentHeight()
            .padding(PaddingValues(horizontal = 12.dp, vertical = 4.dp))
            .clickable { cardClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardColors(
            contentColor = MaterialTheme.colorScheme.tertiary,
            containerColor = MaterialTheme.colorScheme.tertiary,
            disabledContentColor = MaterialTheme.colorScheme.tertiary,
            disabledContainerColor = MaterialTheme.colorScheme.tertiary
        )
    ) {
        Column(
            modifier = Modifier.padding(PaddingValues(horizontal = 12.dp, vertical = 4.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 置顶标签或图标
                if (data.type == 1) {
                    Text(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.error,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 2.dp),
                        text = stringResource(R.string.article_pinned),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.icon_article_logo),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                        contentDescription = stringResource(R.string.article_icon),
                        modifier = Modifier.size(18.dp)
                    )
                }

                // 作者信息（单行省略）
                Text(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f), // 使用 weight 占据剩余空间，配合 maxLines 实现省略
                    text = if (data.author.isNotEmpty()) {
                        stringResource(R.string.article_author, data.author)
                    } else {
                        stringResource(R.string.article_share_user, data.shareUser)
                    },
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.surface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // 时间信息
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = data.niceDate,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onTertiary
                )
            }


            // 标题（多行省略）
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = data.title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.surface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )



            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    text = "${data.chapterName}/${data.superChapterName}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(
                    modifier = Modifier.weight(1f)
                )
                Image(
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { favoriteClick() },
                    painter = painterResource(id = if(data.collect){
                        R.drawable.icon_heart_blue
                    }else{
                        R.drawable.icon_heart_grey
                    }),
                    contentDescription = stringResource(R.string.article_favorite_icon)
                )
            }

        }
    }
}


