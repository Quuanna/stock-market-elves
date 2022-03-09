package com.mitake.finacialidea

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mitake.finacialidea.adapter.StockPageAdapter
import com.mitake.finacialidea.data.model.StockInfo
import com.mitake.finacialidea.data.constant.UserSelectType
import com.mitake.finacialidea.databinding.ActivityInformationBinding
import com.mitake.finacialidea.databinding.ActivitySteadyBinding
import com.mitake.finacialidea.fragment.*
import com.mitake.finacialidea.fragment.valueType.EvaluationAnalysisFragment
import com.mitake.finacialidea.fragment.valueType.ManagementCapacityFragment
import com.mitake.finacialidea.fragment.valueType.ProfitabilityFragment
import com.mitake.finacialidea.manager.valueType.SteadyTypeManager
import com.mitake.finacialidea.manager.valueType.ValueTypeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 穩定型股票
 */
class SteadyTypeActivity : AppCompatActivity(), View.OnClickListener,
    StockPageAdapter.OnStockClickListener {

    companion object {
        private const val STYLE_TYPE = "STYLE_TYPE"
        fun getActivityIntent(
            context: Context,
            type: UserSelectType
        ): Intent {
            return Intent(context, SteadyTypeActivity::class.java)
                .putExtra(STYLE_TYPE, type)
        }
    }


    private var mStockList: ArrayList<StockInfo> = arrayListOf()
    private var mStockInfoItme: StockInfo? = null
    private lateinit var mAdapter: StockPageAdapter
    private lateinit var mBinding: ActivitySteadyBinding
    private val mStyleType: UserSelectType by lazy { initStyleType() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySteadyBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initView()
        initData()
    }

    private fun initView() {
        mBinding.btnEvaluationAnalysis.setOnClickListener(this)
        mBinding.btnManagementCapacity.setOnClickListener(this)
        mBinding.btnProfitability.setOnClickListener(this)
        mBinding.btnStockInfo.setOnClickListener(this)
    }

    private fun initData() {
        mBinding.progressBar.isVisible = true

        SteadyTypeManager.getSteadyData {
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.Main) {
                    Log.d("TAG", "篩選完穩定股票 = " + it.size)
                    if (it.size != 0) {

                        initRecycleViewData(it)
                        initSetOneData(it[0])
                        mBinding.fragmentItemView.isVisible = true
                        mBinding.recyclerView.isVisible = true
                        mBinding.layBtnTitle.isVisible = true
                        mBinding.progressBar.isVisible = false
                        mBinding.layNoData.isVisible = false
                    } else {
                        mBinding.layNoData.isVisible = true
                        mBinding.recyclerView.isVisible = false
                        mBinding.layBtnTitle.isVisible = false
                        mBinding.fragmentItemView.isVisible = false
                        mBinding.progressBar.isVisible = false
                    }
                }
            }
        }
    }


    private fun initRecycleViewData(list: List<StockInfo>) {
        mStockList.addAll(list)
        mAdapter = StockPageAdapter(mStockList, this)
        mBinding.recyclerView.adapter = mAdapter
        mBinding.recyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mAdapter.updateData(list)
    }

    private fun initStyleType(): UserSelectType {
        return intent.getSerializableExtra(STYLE_TYPE) as UserSelectType
    }

    private fun initSetOneData(stockItem: StockInfo) {
        mBinding.layBtnTitle.isVisible = true
        mBinding.progressBar.isVisible = true
        getAssessData(stockItem)
        mStockInfoItme = stockItem
    }

    // 股票切換
    override fun onStockClick(stockItem: StockInfo, position: Int) {
        // 點擊切換資料
        mAdapter.setThisPosition(position)
        mBinding.layBtnTitle.isVisible = true
        mBinding.progressBar.isVisible = true
        getAssessData(stockItem)
        mStockInfoItme = stockItem
    }

    // 個股資料切換
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnEvaluationAnalysis -> {
                getAssessData(mStockInfoItme)
            }
            R.id.btnManagementCapacity -> {
                getManagementCapacity(mStockInfoItme)
            }
            R.id.btnProfitability -> {
                getProfitability(mStockInfoItme)
            }
            R.id. btnYield -> {
                getYield(mStockInfoItme)
            }
            R.id.btnStockInfo -> {
                getStockInfo(mStockInfoItme)
            }
        }
    }

    private fun showDataFragment(data: StockInfo?, fragment: Fragment) {
        val bundle = Bundle()
        bundle.putSerializable(EvaluationAnalysisFragment.EVALUATION_ANALYSIS, data)
        fragment.arguments = bundle
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentItemView, fragment)
        fragmentTransaction.commit()
    }

    /**
     * 投資評估
     */
    fun getAssessData(stockItem: StockInfo?) {
        mBinding.btnEvaluationAnalysis.setBackgroundColor(
            resources.getColor(
                R.color.btn_title_background,
                null
            )
        )
        mBinding.btnManagementCapacity.setBackgroundColor(resources.getColor(R.color.gray, null))
        mBinding.btnProfitability.setBackgroundColor(resources.getColor(R.color.gray, null))
        mBinding.btnStockInfo.setBackgroundColor(resources.getColor(R.color.gray, null))
        mBinding.btnYield.setBackgroundColor(resources.getColor(R.color.gray, null))
        showDataFragment(stockItem, EvaluationAnalysisFragment())
        mBinding.progressBar.isVisible = false
    }

    /**
     *  經營能力
     */
    fun getManagementCapacity(stockItem: StockInfo?) {
        mBinding.btnEvaluationAnalysis.setBackgroundColor(resources.getColor(R.color.gray, null))
        mBinding.btnManagementCapacity.setBackgroundColor(
            resources.getColor(
                R.color.btn_title_background,
                null
            )
        )
        mBinding.btnProfitability.setBackgroundColor(resources.getColor(R.color.gray, null))
        mBinding.btnStockInfo.setBackgroundColor(resources.getColor(R.color.gray, null))
        mBinding.btnYield.setBackgroundColor(resources.getColor(R.color.gray, null))
        showDataFragment(stockItem, ManagementCapacityFragment())
        mBinding.progressBar.isVisible = false
    }

    /**
     * 獲利能力
     */
    fun getProfitability(stockItem: StockInfo?) {
        mBinding.btnEvaluationAnalysis.setBackgroundColor(resources.getColor(R.color.gray, null))
        mBinding.btnManagementCapacity.setBackgroundColor(resources.getColor(R.color.gray, null))
        mBinding.btnProfitability.setBackgroundColor(
            resources.getColor(
                R.color.btn_title_background,
                null
            )
        )
        mBinding.btnStockInfo.setBackgroundColor(resources.getColor(R.color.gray, null))
        mBinding.btnYield.setBackgroundColor(resources.getColor(R.color.gray, null))
        showDataFragment(stockItem, ProfitabilityFragment())
        mBinding.progressBar.isVisible = false
    }

    /**
     * 個股資訊
     */
    fun getStockInfo(stockItem: StockInfo?) {
        mBinding.btnEvaluationAnalysis.setBackgroundColor(resources.getColor(R.color.gray, null))
        mBinding.btnManagementCapacity.setBackgroundColor(resources.getColor(R.color.gray, null))
        mBinding.btnProfitability.setBackgroundColor(resources.getColor(R.color.gray, null))
        mBinding.btnStockInfo.setBackgroundColor(
            resources.getColor(
                R.color.btn_title_background,
                null
            )
        )
        mBinding.btnYield.setBackgroundColor(resources.getColor(R.color.gray, null))
        showDataFragment(stockItem, BasicInfoFragment())
        mBinding.progressBar.isVisible = false
    }

    /**
     * 殖利率
     */
    fun getYield(stockItem: StockInfo?) {
        mBinding.btnEvaluationAnalysis.setBackgroundColor(resources.getColor(R.color.gray, null))
        mBinding.btnManagementCapacity.setBackgroundColor(resources.getColor(R.color.gray, null))
        mBinding.btnProfitability.setBackgroundColor(resources.getColor(R.color.gray, null))
        mBinding.btnStockInfo.setBackgroundColor(resources.getColor(R.color.gray, null))
        mBinding.btnYield.setBackgroundColor(resources.getColor(R.color.btn_title_background, null))
        showDataFragment(stockItem, BasicInfoFragment())
        mBinding.progressBar.isVisible = false
    }
}
