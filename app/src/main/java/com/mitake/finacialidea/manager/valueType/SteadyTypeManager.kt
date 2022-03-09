package com.mitake.finacialidea.manager.valueType

import android.util.Log
import com.google.gson.Gson

import com.mitake.api.IDataCallback
import com.mitake.api.quote.`object`.*
import com.mitake.api.quote.request.*
import com.mitake.api.quote.request.sks.DividendHistoryRequest
import com.mitake.api.quote.response.sks.DividendHistoryResponse
import com.mitake.api.quote.response.*
import com.mitake.finacialidea.R
import com.mitake.finacialidea.data.model.DividendHistories
import com.mitake.finacialidea.data.model.StockInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.ArrayList
import kotlin.coroutines.resume


/**
 * 穩定型股票
 */
object SteadyTypeManager {

    private val TAG = ValueTypeManager::class.java.simpleName


    /**
     * 篩選股票
     */
    fun getSteadyData(callback: (ArrayList<StockInfo>) -> Unit) {
        CoroutineScope(Dispatchers.Default).launch {
            val mStockList: ArrayList<StockInfo> = arrayListOf()
            if (getTCABjrankn7().isNotEmpty()) {
                for (i in 0 until getTCABjrankn7().size) {
                    val item = getTCABjrankn7()[i]
                    getDividendHistory(item.code) { code, _ ->  // 現金股利殖利率 >= 5、連續發股利超過10年
                        code?.also { stockCode ->
                            getTCSpNewFinRatio3(stockCode) { code, _ -> //  營收成長率(正)
                                code?.also {
                                    getTCSpNewFinRatio1(code) { roeCode, ratio1List -> // 股東權益報酬率 >= 6
                                        roeCode?.also {
                                            getTCSpNewJDiag(roeCode) { returnCode, _ -> // 股價淨值比(PBR)<=2 、 本益比<=20
                                                returnCode?.also {
                                                    getTCSpNewMDealer(stockCode) { itemInfo ->
                                                        itemInfo?.also {
                                                            if (itemInfo[0].change.contains("-")) {
                                                                mStockList.add(
                                                                    StockInfo(
                                                                        ratio1List!![0].yearSeason,
                                                                        item.name,
                                                                        item.code,
                                                                        itemInfo[0].closePrice,
                                                                        R.drawable.ic_baseline_arrow_downward_24,
                                                                        false,
                                                                        itemInfo[0].change
                                                                    )
                                                                )
                                                            } else {
                                                                mStockList.add(
                                                                    StockInfo(
                                                                        ratio1List!![0].yearSeason,
                                                                        item.name,
                                                                        item.code,
                                                                        itemInfo[0].closePrice,
                                                                        R.drawable.ic_baseline_arrow_upward_24,
                                                                        true,
                                                                        itemInfo[0].change
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
                Log.d("TAG", "經營績效-殖利率 異常")
            }
        }
    }


    /**
     * 經營績效-殖利率
     */
    suspend fun getTCABjrankn7(): ArrayList<TCABjrank7Item> {
        return suspendCancellableCoroutine { coroutine ->
            TCABjrankn7Request().send("1", object : IDataCallback<Any?> {
                override fun callback(response: Any?) {
                    //取得返回數據
                    val returnResponse = response as TCABjrank7Response?
                    if (returnResponse != null) {
                        Log.d("TAG3", "穩定型股票 START" + returnResponse.items.size)
                        coroutine.resume(returnResponse.items)
                    } else {
                        coroutine.resume(arrayListOf())
                    }
                }

                override fun exception(code: Int, message: String) {
                    Log.d("TAG3", "穩定型股票Error:$code:$message")
                }
            })
        }
    }

    /**
     * 取得歷史股利
     */
    fun getDividendHistory(
        code: String,
        result: (code: String?, ArrayList<DividendHistories>?) -> Unit
    ) {
        val request = DividendHistoryRequest()

        request.send(code, object : IDataCallback<DividendHistoryResponse> {

            override fun callback(response: DividendHistoryResponse?) {
                if (response != null) {
                    Log.d("TAG4", "取得歷史股利 START" + code + "=" + Gson().toJson(response))

                    val data = response.data.dividendHistories
                    val list = arrayListOf<DividendHistories>()

                    for (i in 0 until data.size) {
                        val item = data[i]

                        if (item.cashDividendYield >= 5) {
                            if (item.cashDividend > 0 || item.stockDividend > 0) {
                                list.add(
                                    DividendHistories(
                                        item.cashDividend,
                                        item.cashDividendPayoutRatio,
                                        item.cashDividendYield,
                                        item.stockDividend,
                                        item.year
                                    )
                                )
                            }
                        }
                    }
                    result(code, list)
                } else {
                    result(null, null)
                }
            }

            override fun exception(code: Int, message: String?) {
                Log.d("TAG3", "取得歷史股利 Error:$code:$message")
                result(null, null)
            }
        })
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
        stockCode: String,
        result: (code: String?, ArrayList<TCSpNewFinRatio1Item>?) -> Unit
    ) {
        TCSpNewFinRatio1Request().send(stockCode, object : IDataCallback<Any?> {
            override fun callback(response: Any?) {
                //取得返回數據
                val returnResponse = response as TCSpNewFinRatio1Response?
                if (returnResponse != null) {
                    val roelist = arrayListOf<Float>() //股東權益報酬率
                    for (i in 0 until returnResponse.items.subList(0, 11).size) {
                        val item = returnResponse.items[i]
                        roelist.add(item.roe.toFloat())
                    }
                    val roeY3Average = roelist.average()
                    if (roeY3Average >= 6) {
                        result(stockCode, returnResponse.items)
                    } else {
                        result(null, null)
                    }
                } else {
                    result(null, null)
                }
            }

            override fun exception(code: Int, message: String) {
                Log.d("TAG", "獲利能力 Error:$code:$message")
                result(null, null)
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
//        }
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
        stockCode: String?,
        result: (ArrayList<TCSpNewMDealerItem>?) -> Unit
    ) {
        TCSpNewMDealerRequest().send(stockCode, object : IDataCallback<Any?> {
            override fun callback(response: Any?) {
                //取得返回數據
                val returnResponse = response as TCSpNewMDealerResponse?
                if (returnResponse != null) {
                    result(returnResponse.items)
                } else {
                    result(null)
                }


            }

            override fun exception(code: Int, message: String) {
                Log.d("TAG", "主力進出 Error:$code:$message")
                result(null)
            }
        })
    }

}