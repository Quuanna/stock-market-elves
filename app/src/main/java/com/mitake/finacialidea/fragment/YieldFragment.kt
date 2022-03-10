package com.mitake.finacialidea.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mitake.api.IDataCallback
import com.mitake.api.quote.`object`.TCSpNewCompanyItem
import com.mitake.api.quote.request.TCSpNewCompanyRequest
import com.mitake.api.quote.response.TCSpNewCompanyResponse
import com.mitake.finacialidea.data.model.StockInfo
import com.mitake.finacialidea.databinding.FragmentBasicInfoBinding
import com.mitake.finacialidea.databinding.FragmentYieldBinding
import com.mitake.finacialidea.manager.valueType.SteadyTypeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

class YieldFragment : Fragment() {

    companion object {
        const val EVALUATION_ANALYSIS = "EVALUATION_ANALYSIS"
    }

    private lateinit var mBinding: FragmentYieldBinding
    private val binding get() = mBinding
    private val mStockInfoList: StockInfo by lazy { initStockInfo() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentYieldBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mStockInfoList.code?.let {
            SteadyTypeManager.getDividendHistory(mStockInfoList.code!!) { _, itemList ->
                Log.d("TAG2", "股利" + itemList.toString())
                CoroutineScope(Dispatchers.Main).launch {
                    if(itemList?.size != 0) {
                        itemList?.let { it ->
                            binding.tvYear1.text = it[0].year
                            binding.tvYear2.text = it[1].year
                            binding.tvYear3.text = it[2].year
                            binding.tvYear4.text = it[3].year
                            binding.tvYear5.text = it[4].year

                            // 現金股利
                            binding.tvCashDividend1.text = it[0].cashDividend.toString()
                            binding.tvCashDividend2.text = it[1].cashDividend.toString()
                            binding.tvCashDividend3.text = it[2].cashDividend.toString()
                            binding.tvCashDividend4.text = it[3].cashDividend.toString()
                            binding.tvCashDividend5.text = it[4].cashDividend.toString()

                            // 股票股利
                            binding.tvCashDividend12.text =it[0].stockDividend.toString()
                            binding.tvCashDividend22.text =it[1].stockDividend.toString()
                            binding.tvCashDividend32.text =it[2].stockDividend.toString()
                            binding.tvCashDividend42.text =it[3].stockDividend.toString()
                            binding.tvCashDividend52.text =it[4].stockDividend.toString()

                            // 現金股利\n發放率
                            binding.tvCashDividend13.text =it[0].cashDividendPayoutRatio.toString()
                            binding.tvCashDividend23.text =it[1].cashDividendPayoutRatio.toString()
                            binding.tvCashDividend33.text =it[2].cashDividendPayoutRatio.toString()
                            binding.tvCashDividend43.text =it[3].cashDividendPayoutRatio.toString()
                            binding.tvCashDividend53.text =it[4].cashDividendPayoutRatio.toString()

                            // 現金股利\n殖利率
                            binding.tvCashDividend14.text =it[0].cashDividendYield.toString()
                            binding.tvCashDividend24.text =it[1].cashDividendYield.toString()
                            binding.tvCashDividend34.text =it[2].cashDividendYield.toString()
                            binding.tvCashDividend44.text =it[3].cashDividendYield.toString()
                            binding.tvCashDividend54.text =it[4].cashDividendYield.toString()
                        }
                    }
                }
            }
        }
    }

    private fun initStockInfo(): StockInfo {
        val bundle = this.arguments
        return bundle?.getSerializable(EVALUATION_ANALYSIS) as StockInfo
    }

}
