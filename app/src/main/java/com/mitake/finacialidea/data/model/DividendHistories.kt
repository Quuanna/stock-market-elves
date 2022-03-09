package com.mitake.finacialidea.data.model

import java.time.Year

/**
 * {"cashDividend":0.56,"cashDividendPayoutRatio":48.54,"cashDividendYield":0.088,"stockDividend":2.012092E7,"year":"2011"
 */
data class DividendHistories(
    val cashDividend: Float?, // 現金股利
    val cashDividendPayoutRatio: Float?, // 現金股利發放率
    val cashDividendYield: Float?, // 現金股利殖利率
    val stockDividend: Float?, // 股票股利
    val year: String // 年度
)
