package com.ggb.wanandroid.feature.coin.data

/**
 * {
 *         "coinCount": 12,
 *         "date": 1770343683000,
 *         "desc": "2026-02-06 10:08:03 签到 , 积分：10 + 2",
 *         "id": 930758,
 *         "reason": "签到",
 *         "type": 1,
 *         "userId": 171270,
 *         "userName": "zhufx"
 *       }
 * */
data class PersonalCoinData(
    val  coinCount : Int = 0,
    val  date : Long = 0,
    val  desc : String = "",
    val  id : Int = 0,
    val  reason : String = "",
    val  type : Int = 0,
    val  userId : Int = 0,
    val  userName : String = ""
)
