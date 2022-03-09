package com.mitake.finacialidea.data.model.valueType
import java.io.Serializable

/**
 * 價值型 - 評估方式
 */
data class EvaluationAnalysisData(
    val name: String?, // 股名
    val code: String?, // 股票代號
    val date: String?, //時間
    val stockPriceList: ArrayList<Float>?, // 股價近四季
    val epsList: ArrayList<Float>?, // eps近四季
    val afterTaxNetProfitGrowthRate: String?, //	稅後淨利成長率
    var revenueGrowthRate : String?, // 營收年增率
    var totalAssetTurnover : String?, // 總資產週轉率
    var assets : String?, // 總資產週轉率 - 資產
    var ownersEquity : String?, // 總資產週轉率 - 股東權益
    var eps : String?, // EPS
    var roa : String?, // ROA
    var roe : String?, // ROE
    var netValue : String?, // 股價淨值比
    var pERatio : String?, // 本益比
    var yieldratio : String?, // 殖利率
) : Serializable
