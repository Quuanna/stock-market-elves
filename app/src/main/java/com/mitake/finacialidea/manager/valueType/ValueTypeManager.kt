package com.mitake.finacialidea.manager.valueType

import android.util.Log
import com.mitake.api.IDataCallback
import com.mitake.api.quote.`object`.*
import com.mitake.api.quote.request.*
import com.mitake.api.quote.response.*
import com.mitake.finacialidea.R
import com.mitake.finacialidea.data.model.StockInfo
import com.mitake.finacialidea.data.model.valueType.EvaluationAnalysisData
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.resume

/**
 * 價值型投資 - 評估分析
 */
object ValueTypeManager {


    private val TAG = ValueTypeManager::class.java.simpleName

    /**
     * 篩選符合此策略的股票 - 選單
     */
    fun getStockItemList(type: String, callback: (ArrayList<StockInfo>) -> Unit) {

        CoroutineScope(Dispatchers.Default).launch {
            //季EPS創新高 list
            val mStockList: ArrayList<StockInfo> = arrayListOf()
            if (getTCABjrank9(type).isNotEmpty()) {
                Log.d("TAG", "篩選符合此策略的股票 START")
                for (i in 0 until getTCABjrank9(type).size) {
                    val item = getTCABjrank9(type)[i]
                    getTCSpNewFinRatio3(item.code) { code, _ -> // 營收成長率(正)
                        code?.also {
                            getTCSpNewJDiag(code) { returnCode, _ -> // 股價淨值比(PBR)<=2 、 本益比<=20
                                returnCode?.also {
                                    getTCSpNewFinRatio2(returnCode) { stockCode, _ ->  // 負債比率	<= 50、流動比率 >= 150
                                        stockCode?.also {
                                            getTCSpNewFinRatio1(
                                                stockCode,
                                                false
                                            ) { stockCode, ratio1List, _ ->
                                                stockCode?.also {
                                                    getTCSpNewMDealer(stockCode) { it, _ ->
                                                        it?.also {
                                                            if (it[0].change.contains("-")) {
                                                                mStockList.add(
                                                                    StockInfo(
                                                                        ratio1List!![0].yearSeason,
                                                                        item.name,
                                                                        item.code,
                                                                        it[0].closePrice,
                                                                        R.drawable.ic_baseline_arrow_downward_24,
                                                                        false,
                                                                        it[0].change
                                                                    )
                                                                )
                                                            } else {
                                                                mStockList.add(
                                                                    StockInfo(
                                                                        ratio1List!![0].yearSeason,
                                                                        item.name,
                                                                        item.code,
                                                                        it[0].closePrice,
                                                                        R.drawable.ic_baseline_arrow_upward_24,
                                                                        true,
                                                                        it[0].change
                                                                    )
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Log.d("TAG", "篩選符合此策略的股票 END")
                callback(mStockList)
            } else {
                Log.d("TAG", "經營績效-季資料 異常")
            }
        }
    }


    /**
     * 評估面 EvaluationAnalysisData 所有股票清單
     */
    fun getEvaluationAnalysisData(
        stockItems: StockInfo, callback: (EvaluationAnalysisData?) -> Unit
    ) {
        CoroutineScope(Dispatchers.Default).launch {
            // 經營能力
            stockItems.code?.let {
                Log.d("TAG", " 評估面 START")
                getTCSpNewFinRatio3(it) { _, ratio3List ->
                    ratio3List?.also {
                        getTCSpNewAsset(stockItems.code) { assetItemList ->
                            assetItemList?.also {
                                getTCSpNewFinRatio1(
                                    stockItems.code, true
                                ) { _, ratio1List, epsList ->
                                    ratio1List.also {
                                        getTCSpNewJDiag(stockItems.code) { _, newJDiagList ->
                                            newJDiagList?.also {
                                                getTCSpNewMDealer(stockItems.code) { _, priceList ->

                                                    callback(
                                                        EvaluationAnalysisData(
                                                            stockItems.title,
                                                            stockItems.code,
                                                            ratio1List!![0].yearSeason,
                                                            priceList,
                                                            epsList,
                                                            ratio3List[0].afterTaxNetProfitGrowthRate,
                                                            ratio3List[0].revenueGrowthRate,
                                                            ratio3List[0].totalAssetTurnover,
                                                            assetItemList[0].asset,
                                                            assetItemList[0].ownersEquity,
                                                            ratio1List[0].eps,
                                                            ratio1List[0].roa,
                                                            ratio1List[0].roe,
                                                            newJDiagList[0].mPBratio,
                                                            newJDiagList[0].mPEratio,
                                                            newJDiagList[0].mYieldratio
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } ?: run {
                Log.d("TAG", "股票資料 異常")
            }
        }
    }


//---------------------------------------------------------------------------------------------------------
    /**
     * 經營績效-季資料 TCABjrank9
     * 1: 季毛利率排行
     * 2: 季營利率排行
     * 3: 季EPS排行
     * 4: 季EPS創新高
     * 5: 季轉虧為盈
     */
    suspend fun getTCABjrank9(type: String): ArrayList<TCABjrank9Item> {
        return suspendCancellableCoroutine { coroutine ->
            TCABjrankn9Request().send(type, object : IDataCallback<Any?> {
                override fun callback(response: Any?) {
                    //取得返回數據
                    val returnResponse = response as TCABjrank9Response?
                    if (returnResponse != null) {
                        Log.d(TAG, "經營績效-季資料 :" + returnResponse.items.size)
                        coroutine.resume(returnResponse.items)
                    } else {
                        coroutine.resume(arrayListOf())
                    }
                }

                override fun exception(code: Int, message: String) {
                    coroutine.resume(arrayListOf())
                    Log.d(TAG, "季EPS創新高 Error:$code:$message")
                }
            })
        }
    }


    /**
     * 經營能力 TCSpNewFinRatio3
     * request stockCode 商品代碼
     * response 取最近一季的營業成長率
     */
    fun getTCSpNewFinRatio3(
        stockCode: String,
        result: (code: String?, ArrayList<TCSpNewFinRatio3Item>?) -> Unit
    ) {
        TCSpNewFinRatio3Request().send(stockCode, object : IDataCallback<Any?> {
            override fun callback(response: Any?) {
                //取得返回數據
                val returnResponse = response as TCSpNewFinRatio3Response?
                if (returnResponse != null) {
                    if (!returnResponse.items[0].revenueGrowthRate.contains("-")) {
                        result(stockCode, returnResponse.items)
                    } else {
                        result(null, null)
                    }
                } else {
                    result(null, null)
                }

            }

            override fun exception(code: Int, message: String) {
                Log.d(TAG, "經營能力 Error:$code:$message")
                result(null, null)
            }
        })
    }

    /**
     * 獲利能力 TCSpNewFinRatio1
     *  request stockCode 商品代碼
     *
     *  ROA（單季）
     *  ROE （單季）
     *  EPS（單季）
     */
    fun getTCSpNewFinRatio1(
        stockCode: String, isAssess: Boolean,
        result: (
            code: String?, ArrayList<TCSpNewFinRatio1Item>?,
            epsFourSeasons: ArrayList<Float>?
        ) -> Unit
    ) {
        TCSpNewFinRatio1Request().send(stockCode, object : IDataCallback<Any?> {
            override fun callback(response: Any?) {
                //取得返回數據
                val returnResponse = response as TCSpNewFinRatio1Response?
                if (returnResponse != null) {
                    if (isAssess) {
                        val epsList = arrayListOf<Float>()
                        for (i in 0 until returnResponse.items.subList(0, 4).size) {
                            val itemEPS = returnResponse.items[i].eps.toFloat()
                            epsList.add(itemEPS)
                        }
                        result(stockCode, returnResponse.items, epsList)
                    } else {

//                        val list = arrayListOf<Float>() //股東權益報酬率
//                        val list2 = arrayListOf<Float>() // 稅後淨利率
//                        for (i in 0 until returnResponse.items.subList(0, 11).size) {
//                            val item = returnResponse.items[i]
//                            list.add(item.roe.toFloat())
////                            list2.add(item.afterTaxMargin.toFloat())
//                        }
//                        val roeY3Average = list.average()
////                        val afterTaxMarginY3Average2 = list2.average()
//                        if (roeY3Average >= 5) {
                        result(stockCode, returnResponse.items, null)
//                        } else {
//                            result(null, null, null)
//                        }
                    }
                } else {
                    result(null, null, null)
                }
            }

            override fun exception(code: Int, message: String) {
                Log.d("TAG", "獲利能力 Error:$code:$message")
                result(null, null, null)
            }
        })
    }

    /**
     * 財務診斷  TCSpNewJDiag
     * request stockCode 商品代碼
     * 股價淨值比(PBR) = 股價 / 每股淨值
     */
    fun getTCSpNewJDiag(
        stockCode: String,
        result: (code: String?, ArrayList<TCSpNewJDiagItem>?) -> Unit
    ) {
        TCSpNewJDiagRequest().send(stockCode, object : IDataCallback<Any?> {
            override fun callback(response: Any?) {
                //取得返回數據
                val returnResponse = response as TCSpNewJDiagResponse?
                if (returnResponse != null) {
                    Log.d(TAG, "財務診斷= $returnResponse")

                    if (returnResponse.items[0].mPBratio.toFloat() <= 2
                        && returnResponse.items[0].mPEratio.toFloat() <= 20
                    ) {
                        result(stockCode, returnResponse.items)
                    } else {
                        result(null, null)
                    }

                } else {
                    result(null, null)
                }
            }

            override fun exception(code: Int, message: String) {
                Log.d(TAG, "財務診斷 Error:$code:$message")
                result(null, null)
            }
        })
    }

    /**
     *  資產負債表 TCSpNewAsset
     *  request
     *  stockCode 商品代碼
     *  type 資料類型	1: 絕對值
     *
     *  response
     *  財務槓桿 = 資產/ 股東權益
     */
    fun getTCSpNewAsset(
        stockCode: String,
        result: (ArrayList<TCSpNewAssetItem>?) -> Unit
    ) {
        val quoteRequest = TCSpNewAssetRequest()
        quoteRequest.send(stockCode, "1", object : IDataCallback<Any?> {
            override fun callback(response: Any?) {
                //取得返回數據
                val returnResponse = response as TCSpNewAssetResponse?
                if (returnResponse != null) {
                    result(returnResponse.items)
                } else {
                    result(null)
                }
            }

            override fun exception(code: Int, message: String) {
                Log.d(TAG, "資產負債表 Error:$code:$message")
                result(null)
            }
        })
    }

    /**
     *  償債能力
     */
    fun getTCSpNewFinRatio2(
        stockCode: String,
        result: (code: String?, ArrayList<TCSpNewFinRatio2Item>?) -> Unit
    ) {
        val quoteRequest = TCSpNewFinRatio2Request()
        quoteRequest.send(stockCode, object : IDataCallback<Any?> {
            override fun callback(response: Any?) {
                //取得返回數據
                val returnResponse = response as TCSpNewFinRatio2Response?
                if (returnResponse != null) {
                    if (returnResponse.items[0].debtRatio.toFloat() <= 50 && returnResponse.items[0].currentRatio.toFloat() >= 150) {
                        result(stockCode, returnResponse.items)
                    } else {
                        result(null, null)
                    }
                } else {
                    result(null, null)
                }
            }

            override fun exception(code: Int, message: String) {
                Log.d("TAG", "償債能力 Error:$code:$message")
                result(null, null)
            }
        })
    }

    /**
     * 主力進出 TCSpNewMDealer
     * request
     * stockCode 商品代碼
     *
     * response
     * closePrice	String	收盤價
     * change	String	漲跌
     */

    fun getTCSpNewMDealer(
        stockCode: String,
        result: (ArrayList<TCSpNewMDealerItem>?, ArrayList<Float>?) -> Unit
    ) {
        TCSpNewMDealerRequest().send(stockCode, object : IDataCallback<Any?> {
            override fun callback(response: Any?) {
                //取得返回數據
                val returnResponse = response as TCSpNewMDealerResponse?
                if (returnResponse != null) {
                    val priceList = arrayListOf<Float>()
                    for (i in 0 until returnResponse.items.subList(0, 4).size) {
                        val item = returnResponse.items[i].closePrice.toFloat()
                        priceList.add(item)
                    }
                    result(returnResponse.items, priceList)
                } else {
                    result(null, null)
                }


            }

            override fun exception(code: Int, message: String) {
                Log.d("TAG", "主力進出 Error:$code:$message")
                result(null, null)
            }
        })
    }

}


