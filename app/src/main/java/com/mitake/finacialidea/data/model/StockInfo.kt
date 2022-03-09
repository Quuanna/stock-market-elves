package com.mitake.finacialidea.data.model

import java.io.Serializable

data class StockInfo(
    val date: String?, // 年季
    val title: String?,  // 股票名稱
    val code: String?, // 股票代號
    val price: String?, // 股票價格
    val target: Int?, // 漲跌幅圖
    val isTarget: Boolean?, //是否漲跌
    val targetPercentage: String?, // 漲跌幅數字
)  : Serializable
