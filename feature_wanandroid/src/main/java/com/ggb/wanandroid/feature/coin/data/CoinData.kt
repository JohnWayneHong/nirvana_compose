package com.ggb.wanandroid.feature.coin.data

/**
 * "coinCount": 68970,
 *         "level": 690,
 *         "nickname": "",
 *         "rank": "31",
 *         "userId": 7891,
 *         "username": "h**zkp"
 * */
data class CoinData(
    val coinCount : Int = 0,//总积分
    val level : Int = 0,//等级
    val nickname : String = "",
    val rank : String = "",//当前排名
    val userId : Int = 0,
    val username : String = ""
)