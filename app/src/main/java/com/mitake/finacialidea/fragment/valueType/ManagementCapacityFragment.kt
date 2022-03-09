package com.mitake.finacialidea.fragment.valueType

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mitake.api.quote.`object`.TCSpNewFinRatio3Item
import com.mitake.finacialidea.R
import com.mitake.finacialidea.data.model.StockInfo
import com.mitake.finacialidea.databinding.FragmentManagementCapacityBinding
import com.mitake.finacialidea.manager.valueType.ValueTypeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat

/**
 * 經營能力
 */
class ManagementCapacityFragment : Fragment() {

    companion object {
        const val EVALUATION_ANALYSIS = "EVALUATION_ANALYSIS"
    }

    private lateinit var mBinding: FragmentManagementCapacityBinding
    private val binding get() = mBinding
    val mDecimalFormat = DecimalFormat("###.##")
    private val mStockInfoList: StockInfo by lazy { initStockInfo() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentManagementCapacityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mStockInfoList.code?.let {
            ValueTypeManager.getTCSpNewFinRatio3(it) { code, itemList ->
//                Log.d("TAG2", "經營能力" + itemList.toString())

                CoroutineScope(Dispatchers.Main).launch {

                    itemList?.let { it ->
                        // 時間範圍 3年季
                        val list = arrayListOf<TCSpNewFinRatio3Item>()
                        for (item in it) {
                            if (item.type == "A") {
                                list.add(item)
                            }
                        }

                        if (list.size == 11){
                            binding.tvTimeRange.text = list[0].yearSeason + "～" + list.last().yearSeason

                            Log.d("TAG2", "經營能力 0= "+list[0])
                            Log.d("TAG2", "經營能力 last= "+list.last())
                            getGrowthRate(
                                list[0], list.last()
                            ) { growthRate, growthRate2, growthRate3, growthRate4 ->
                                Log.d("TAG2", "存貨周轉率$growthRate")
                                Log.d("TAG2", "應收帳款周轉率$growthRate2")
                                Log.d("TAG2", "固定資產周轉率$growthRate3")
                                Log.d("TAG2", "總資產周轉率$growthRate4")


                                // 存貨周轉率 inventoryTurnover
                                getInventoryTurnover(growthRate)
                                //應收帳款周轉率 receivableTurnover
                                getReceivableTurnover(growthRate2)
                                // 固定資產周轉率 fixedAssetTurnover
                                getFixedAssetTurnover(growthRate3)
                                // 總資產周轉率 totalAssetTurnover
                                getTotalAssetTurnover(growthRate4)
                            }
                        } else if(list.size >= 12){
                            binding.tvTimeRange.text = list[0].yearSeason + "～" + list[11].yearSeason

                            Log.d("TAG2", "經營能力 0= "+list[0])
                            Log.d("TAG2", "經營能力 last= "+list[11])
                            getGrowthRate(
                                list[0], list[11]
                            ) { growthRate, growthRate2, growthRate3, growthRate4 ->
                                Log.d("TAG2", "存貨周轉率$growthRate")
                                Log.d("TAG2", "應收帳款周轉率$growthRate2")
                                Log.d("TAG2", "固定資產周轉率$growthRate3")
                                Log.d("TAG2", "總資產周轉率$growthRate4")


                                // 存貨周轉率 inventoryTurnover
                                getInventoryTurnover(growthRate)
                                //應收帳款周轉率 receivableTurnover
                                getReceivableTurnover(growthRate2)
                                // 固定資產周轉率 fixedAssetTurnover
                                getFixedAssetTurnover(growthRate3)
                                // 總資產周轉率 totalAssetTurnover
                                getTotalAssetTurnover(growthRate4)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getTotalAssetTurnover(growthRate4: Float) {
        if (growthRate4.toString().startsWith("-")) {
            binding.tvExplanation04.text =
                getString(R.string.value_term_management_capacity_subtitle_04_final)
            binding.tvExplanation04.setTextColor(
                resources.getColor(
                    R.color.green,
                    null
                )
            )
        } else {
            binding.tvExplanation04.text =
                getString(R.string.value_term_management_capacity_subtitle_04_final)
            binding.tvExplanation04.setTextColor(
                resources.getColor(
                    R.color.red,
                    null
                )
            )
        }
        binding.tvGrowthRate04.text = mDecimalFormat.format(growthRate4) + "%"
    }

    private fun getFixedAssetTurnover(growthRate3: Float) {
        if (growthRate3.toString().startsWith("-")) {
            binding.tvExplanation03.text =
                getString(R.string.value_term_management_capacity_subtitle_03_low)
            binding.tvExplanation03.setTextColor(
                resources.getColor(
                    R.color.green,
                    null
                )
            )
        } else {
            binding.tvExplanation03.text =
                getString(R.string.value_term_management_capacity_subtitle_03_high)
            binding.tvExplanation03.setTextColor(
                resources.getColor(
                    R.color.red,
                    null
                )
            )
        }
        binding.tvGrowthRate03.text = mDecimalFormat.format(growthRate3) + "%"
    }

    private fun getReceivableTurnover(growthRate2: Float) {
        if (growthRate2.toString().startsWith("-")) {
            binding.tvExplanation02.text =
                getString(R.string.value_term_management_capacity_subtitle_02_low)
            binding.tvExplanation02.setTextColor(
                resources.getColor(
                    R.color.green,
                    null
                )
            )
        } else {
            binding.tvExplanation02.text =
                getString(R.string.value_term_management_capacity_subtitle_02_high)
            binding.tvExplanation02.setTextColor(
                resources.getColor(
                    R.color.red,
                    null
                )
            )
        }
        binding.tvGrowthRate02.text = mDecimalFormat.format(growthRate2) + "%"
    }

    private fun getInventoryTurnover(growthRate: Float) {
        if (growthRate.toString().startsWith("-")) {
            binding.tvExplanation.text =
                getString(R.string.value_term_management_capacity_subtitle_01_low)
            binding.tvExplanation.setTextColor(
                resources.getColor(
                    R.color.green,
                    null
                )
            )
        } else {
            binding.tvExplanation.text =
                getString(R.string.value_term_management_capacity_subtitle_01_high)
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
        new: TCSpNewFinRatio3Item,
        old: TCSpNewFinRatio3Item,
        callBack: (growthRate: Float, growthRate2: Float, growthRate3: Float, growthRate4: Float) -> Unit
    ) {
        // (當年營收 – 去年營收）÷ 去年營收 x 100%
        var inventoryRate = 0f // 存貨周轉率
        var receivableRate = 0f // 應收帳款周轉率
        var fixedAssetRate = 0f // 固定資產周轉率
        var totalAssetRate = 0f // 總資產周轉率

        // 存貨周轉率 inventoryTurnover
        if (!new.inventoryTurnover.isNullOrEmpty() && !old.inventoryTurnover.isNullOrEmpty()) {
            val inventoryNew = new.inventoryTurnover?.toFloat()
            val inventoryOld = old.inventoryTurnover?.toFloat()
            if (inventoryNew != null && inventoryNew != 0f && inventoryOld != null && inventoryOld != 0f) {
                inventoryRate = ((inventoryNew - inventoryOld) / inventoryOld) * 100
            }
        }

        //應收帳款周轉率 receivableTurnover
        if (!new.receivableTurnover.isNullOrEmpty() && !old.receivableTurnover.isNullOrEmpty()) {
            val receivableNew = new.receivableTurnover?.toFloat()
            val receivableOld = old.receivableTurnover?.toFloat()
            if (receivableNew != null && receivableNew != 0f && receivableOld != null && receivableOld != 0f) {
                receivableRate = ((receivableNew - receivableOld) / receivableOld) * 100
            }
        }


        // 固定資產周轉率 fixedAssetTurnover
        if (!new.fixedAssetTurnover.isNullOrEmpty() && !old.fixedAssetTurnover.isNullOrEmpty()) {
            val fixedAssetNew = new.fixedAssetTurnover?.toFloat()
            val fixedAssetOld = old.fixedAssetTurnover?.toFloat()
            if (fixedAssetNew != null && fixedAssetNew != 0f && fixedAssetOld != null && fixedAssetOld != 0f) {
                fixedAssetRate = ((fixedAssetNew - fixedAssetOld) / fixedAssetOld) * 100
            }
        }

        // 總資產周轉率 totalAssetTurnover
        if (!new.totalAssetTurnover.isNullOrEmpty() && !old.totalAssetTurnover.isNullOrEmpty()) {
            val totalAssetNew = new.totalAssetTurnover?.toFloat()
            val totalAssetOld = old.totalAssetTurnover?.toFloat()

            if (totalAssetNew != null && totalAssetNew != 0f && totalAssetOld != null && totalAssetOld != 0f) {
                totalAssetRate = ((totalAssetNew - totalAssetOld) / totalAssetOld) * 100
            }
        }

        callBack(inventoryRate, receivableRate, fixedAssetRate, totalAssetRate)
    }

}