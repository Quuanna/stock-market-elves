package com.mitake.finacialidea.fragment.valueType

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mitake.finacialidea.R
import com.mitake.finacialidea.data.model.StockInfo
import com.mitake.finacialidea.databinding.FragmentEvaluationAnalysisBinding
import com.mitake.finacialidea.manager.valueType.ValueTypeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat

/**
 * 評估分析
 */
class EvaluationAnalysisFragment : Fragment() {

    companion object {
        const val EVALUATION_ANALYSIS = "EVALUATION_ANALYSIS"
    }

    private lateinit var mBinding: FragmentEvaluationAnalysisBinding
    private val binding get() = mBinding
    val mDecimalFormat = DecimalFormat("###.##")
    private val mStockInfoList: StockInfo by lazy { initStockInfo() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentEvaluationAnalysisBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        ValueTypeManager.getEvaluationAnalysisData(mStockInfoList) {
            CoroutineScope(Dispatchers.Main).launch {
                Log.d("TAG", "基本面資料 = $it")
                Log.d("TAG", " 評估面 END")

                binding.date.text = it?.date
                // 經營能力
                binding.tvRevenueGrowthRate.text = it?.revenueGrowthRate
                binding.tvTotalAssetTurnover.text = it?.totalAssetTurnover

                if (it?.assets != null && it.ownersEquity != null) {
                    val assets = DecimalFormat().parse(it.assets!!)?.toDouble()
                    val ownersEquity = DecimalFormat().parse(it.ownersEquity!!)?.toDouble()
                    val financialLeverage = assets!! / ownersEquity!!
                    mBinding?.tvFinancialLeverage?.text = mDecimalFormat.format(financialLeverage)
                }
                // 賺錢能力
                binding.tvEPS.text = it?.eps
                binding.tvROA.text = it?.roa
                binding.tvROE.text = it?.roe

                // 是不適合買入
                binding.tvNetValue.text = mDecimalFormat.format(it?.netValue?.toDouble())
                binding.tvYieldratio.text = mDecimalFormat.format(it?.yieldratio?.toDouble())

                // 本益成長比(PEG)＝本益比 / 稅後淨利成長率
                it?.run {
                    binding.tvPEratio.text = it.pERatio
                    val PEG =
                        mDecimalFormat.format(it.pERatio?.toFloat()!! / it.afterTaxNetProfitGrowthRate?.toFloat()!!)
                            .toFloat()
                    Log.d("TAG", "基本面資料 本益成長比 =$PEG")

                    when {
                        PEG < 0.75 -> {
                            binding.tvStockPrice.text = "股價被低估(可買進)"
                            binding.tvStockPrice.setTextColor(
                                resources.getColor(
                                    R.color.red,
                                    null
                                )
                            )
                        }
                        PEG >= 1.0 -> {
                            binding.tvStockPrice.text = "股價合理(觀望)"
                            binding.tvStockPrice.setTextColor(
                                resources.getColor(
                                    R.color.red,
                                    null
                                )
                            )
                        }

                        PEG > 1.2 -> {
                            binding.tvStockPrice.text = "股價被高估(賣出)"
                            binding.tvStockPrice.setTextColor(
                                resources.getColor(
                                    R.color.red,
                                    null
                                )
                            )
                        }
                    }
                }
            }
        }
    }


    private fun getpEratio(
        stockPriceList: ArrayList<Float>,
        epsList: ArrayList<Float>,
        callback: (ArrayList<Float>) -> Unit
    ) {
        val pERatioList = arrayListOf<Float>()
        val list = arrayListOf<Float>()

        for (i in 0 until stockPriceList.size) {
            val item = stockPriceList[i]
            pERatioList.add(
                mDecimalFormat.format(item).toFloat() / mDecimalFormat.format(epsList[i])
                    .toFloat()
            )
        }
        for (item in pERatioList) {
            list.add(mDecimalFormat.format(item).toFloat())

        }
        callback(list)
    }

    private fun initStockInfo(): StockInfo {
        val bundle = this.arguments
        return bundle?.getSerializable(EVALUATION_ANALYSIS) as StockInfo
    }

}