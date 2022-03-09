package com.mitake.finacialidea.fragment.valueType

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mitake.api.quote.`object`.TCSpNewFinRatio1Item
import com.mitake.api.quote.`object`.TCSpNewFinRatio3Item
import com.mitake.finacialidea.R
import com.mitake.finacialidea.data.model.StockInfo
import com.mitake.finacialidea.databinding.FragmentManagementCapacityBinding
import com.mitake.finacialidea.databinding.FragmentProfitabilityBinding
import com.mitake.finacialidea.manager.valueType.ValueTypeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat

/**
 * 獲利能力
 */
class ProfitabilityFragment : Fragment() {

    companion object {
        const val EVALUATION_ANALYSIS = "EVALUATION_ANALYSIS"
    }

    private lateinit var mBinding: FragmentProfitabilityBinding
    private val binding get() = mBinding
    val mDecimalFormat = DecimalFormat("###.##")
    private val mStockInfoList: StockInfo by lazy { initStockInfo() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentProfitabilityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mStockInfoList.code?.let {
            ValueTypeManager.getTCSpNewFinRatio1(it, false) { code, itemList, _->
                Log.d("TAG2", "獲利能力" + itemList.toString())
                CoroutineScope(Dispatchers.Main).launch {
                    itemList?.let { it ->
                        // 時間範圍 3年季
                        val list = arrayListOf<TCSpNewFinRatio1Item>()
                        for (item in it) {
                            if (item.type == "A") {
                                list.add(item)
                            }
                        }

                        if (list.size == 11) {
                            binding.tvTimeRange.text = list[0].yearSeason + "～" + list.last().yearSeason

                            Log.d("TAG2", "獲利能力 0= " + list[0])
                            Log.d("TAG2", "獲利能力 last= " + list.last())

                            getGrowthRate(
                                list[0], list.last()
                            ) { growthRate, growthRate2, growthRate3 ->
                                Log.d("TAG2", "營業毛利率$growthRate")
                                Log.d("TAG2", "營業利益率$growthRate2")
                                Log.d("TAG2", "稅後淨利率$growthRate3")

                                // 營業毛利率
                                getGrossProfitMargin(growthRate)
                                //營業利益率
                                getOperatingProfitRatio(growthRate2)
                                // 稅後淨利率
                                getAfterTaxMargin(growthRate3)
                            }
                        } else if (list.size > 12){
                            binding.tvTimeRange.text = list[0].yearSeason + "～" + list[11].yearSeason


                            Log.d("TAG2", "獲利能力 0= " + list[0])
                            Log.d("TAG2", "獲利能力 last= " + list[11])

                            getGrowthRate(
                                list[0], list[11]
                            ) { growthRate, growthRate2, growthRate3 ->
                                Log.d("TAG2", "營業毛利率$growthRate")
                                Log.d("TAG2", "營業利益率$growthRate2")
                                Log.d("TAG2", "稅後淨利率$growthRate3")

                                // 營業毛利率
                                getGrossProfitMargin(growthRate)
                                //營業利益率
                                getOperatingProfitRatio(growthRate2)
                                // 稅後淨利率
                                getAfterTaxMargin(growthRate3)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getAfterTaxMargin(growthRate3: Float) {
        if (growthRate3.toString().startsWith("-")) {
            binding.tvExplanation03.text =
                getString(R.string.value_term_profitability_subtitle_03_low)
            binding.tvExplanation03.setTextColor(
                resources.getColor(
                    R.color.green,
                    null
                )
            )
        } else {
            binding.tvExplanation03.text =
                getString(R.string.value_term_profitability_subtitle_03_high)
            binding.tvExplanation03.setTextColor(
                resources.getColor(
                    R.color.red,
                    null
                )
            )
        }
        binding.tvGrowthRate03.text = mDecimalFormat.format(growthRate3) + "%"
    }

    private fun getOperatingProfitRatio(growthRate2: Float) {
        if (growthRate2.toString().startsWith("-")) {
            binding.tvExplanation02.text =
                getString(R.string.value_term_profitability_subtitle_02_low)
            binding.tvExplanation02.setTextColor(
                resources.getColor(
                    R.color.green,
                    null
                )
            )
        } else {
            binding.tvExplanation02.text =
                getString(R.string.value_term_profitability_subtitle_02_high)
            binding.tvExplanation02.setTextColor(
                resources.getColor(
                    R.color.red,
                    null
                )
            )
        }
        binding.tvGrowthRate02.text = mDecimalFormat.format(growthRate2) + "%"
    }

    private fun getGrossProfitMargin(growthRate: Float) {
        if (growthRate.toString().startsWith("-")) {
            binding.tvExplanation.text =
                getString(R.string.value_term_profitability_subtitle_01_low)
            binding.tvExplanation.setTextColor(
                resources.getColor(
                    R.color.green,
                    null
                )
            )
        } else {
            binding.tvExplanation.text =
                getString(R.string.value_term_profitability_subtitle_01_high)
            binding.tvExplanation.setTextColor(
                resources.getColor(
                    R.color.red,
                    null
                )
            )
        }
        binding.tvGrowthRate.text = mDecimalFormat.format(growthRate) + "%"
    }

    private fun initStockInfo(): StockInfo {
        val bundle = this.arguments
        return bundle?.getSerializable(EVALUATION_ANALYSIS) as StockInfo
    }

    private fun getGrowthRate(
        new: TCSpNewFinRatio1Item,
        old: TCSpNewFinRatio1Item,
        callBack: (growthRate: Float, growthRate2: Float, growthRate3: Float) -> Unit
    ) {
        // (當年營收 – 去年營收）÷ 去年營收 x 100%
        var grossProfitMarginRate = 0f // 營業毛利率
        var operatingProfitRate = 0f // 營業利益率
        var afterTaxMarginRate = 0f // 稅後淨利率

        // 營業毛利率 grossProfitMargin
        if (!new.grossProfitMargin.isNullOrEmpty() && !old.grossProfitMargin.isNullOrEmpty()) {
            val grossProfitNew = new.grossProfitMargin?.toFloat()
            val grossProfitMarginOld = old.grossProfitMargin?.toFloat()
            if (grossProfitNew != null && grossProfitNew != 0f && grossProfitMarginOld != null && grossProfitMarginOld != 0f) {
                grossProfitMarginRate =
                    ((grossProfitNew - grossProfitMarginOld) / grossProfitMarginOld) * 100
            }
        }

        //營業利益率 operatingProfitRatio
        if (!new.operatingProfitRatio.isNullOrEmpty() && !old.operatingProfitRatio.isNullOrEmpty()) {
            val operatingProfitNew = new.operatingProfitRatio?.toFloat()
            val operatingProfitOld = old.operatingProfitRatio?.toFloat()
            if (operatingProfitNew != null && operatingProfitNew != 0f && operatingProfitOld != null && operatingProfitOld != 0f) {
                operatingProfitRate =
                    ((operatingProfitNew - operatingProfitOld) / operatingProfitOld) * 100
            }
        }

        // 稅後淨利率 afterTaxMargin
        if (!new.afterTaxMargin.isNullOrEmpty() && !old.afterTaxMargin.isNullOrEmpty()) {
            val afterTaxMarginNew = new.afterTaxMargin?.toFloat()
            val afterTaxMarginOld = old.afterTaxMargin?.toFloat()
            if (afterTaxMarginNew != null && afterTaxMarginNew != 0f && afterTaxMarginOld != null && afterTaxMarginOld != 0f) {
                afterTaxMarginRate =
                    ((afterTaxMarginNew - afterTaxMarginOld) / afterTaxMarginOld) * 100
            }
        }

        callBack(grossProfitMarginRate, operatingProfitRate, afterTaxMarginRate)
    }

}