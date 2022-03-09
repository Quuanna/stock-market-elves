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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

/**
 * 個股資訊
 */
class BasicInfoFragment : Fragment() {

    companion object {
        const val EVALUATION_ANALYSIS = "EVALUATION_ANALYSIS"
    }

    private lateinit var mBinding: FragmentBasicInfoBinding
    private val binding get() = mBinding
    private val mStockInfoList: StockInfo by lazy { initStockInfo() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentBasicInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mStockInfoList.code?.let {
            getStockInfo(it) { itemList->
                Log.d("TAG2", "個股資訊" + itemList.toString())
                CoroutineScope(Dispatchers.Main).launch {
                    itemList?.let { it ->
                        binding.tvContent1.text = it[0].companyName
                        binding.tvContent2.text = it[0].paidInCapital
                        binding.tvContent3.text = it[0].listed
                        binding.tvContent4.text = it[0].industryShare
                        binding.tvContent5.text = it[0].groupShare
                        binding.tvContent6.text = it[0].conceptStock
                        binding.tvContent7.text = it[0].mainBusiness
                    }
                }
            }
        }
    }

    private fun initStockInfo(): StockInfo {
        val bundle = this.arguments
        return bundle?.getSerializable(EVALUATION_ANALYSIS) as StockInfo
    }


    private fun getStockInfo(stockCode: String, callback: (ArrayList<TCSpNewCompanyItem>?) -> Unit) {
        val quoteRequest = TCSpNewCompanyRequest()
        quoteRequest.send(stockCode, "1", object : IDataCallback<Any?> {
            override fun callback(response: Any?) {
                //取得返回數據
                val returnResponse = response as TCSpNewCompanyResponse?
                if (returnResponse != null) {
                    callback(returnResponse.items)
                }
            }

            override fun exception(code: Int, message: String) {
                Log.d("TAG", "個股資訊 Error:$code:$message")
            }
        })
    }
}