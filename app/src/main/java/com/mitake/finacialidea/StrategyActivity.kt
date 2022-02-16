package com.mitake.finacialidea

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mitake.finacialidea.data.constant.UserSelectType
import com.mitake.finacialidea.databinding.ActivityInformationBinding
import com.mitake.finacialidea.databinding.ActivityStrategyBinding

/**
 *  說明策略
 */
class StrategyActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        private const val STYLE_TYPE = "STYLE_TYPE"
        fun getActivityIntent(context: Context, type: UserSelectType): Intent {
            return Intent(context, StrategyActivity::class.java)
                .putExtra(STYLE_TYPE, type)
        }
    }

    private lateinit var mBinding: ActivityStrategyBinding
    private val mStyleType: UserSelectType by lazy { initStyleType() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityStrategyBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initView()
        initData()
    }

    private fun initView() {
        mBinding.backLastPage.setOnClickListener(this)
        mBinding.btnNextPage.setOnClickListener(this)
    }

    private fun initData() {
        when (mStyleType) {
            UserSelectType.SHORT_TERM -> {
                mBinding.tvContent.text = getString(R.string.strategy_content_short_term)
            }
            UserSelectType.VALUE_TYPE -> {
                mBinding.tvContent.text = getString(R.string.strategy_content_value_term)
            }
            UserSelectType.GROWTH_TYPE -> {
                mBinding.tvContent.text = getString(R.string.strategy_content_growth_term)
            }
            UserSelectType.STABLE_INVESTMENT -> {
                mBinding.tvContent.text = getString(R.string.strategy_content_sound_investment)
            }
        }
    }

    private fun initStyleType(): UserSelectType {
        return intent.getSerializableExtra(STYLE_TYPE) as UserSelectType
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.backLastPage -> {
                startActivity(MainActivity.getActivityIntent(this))
                finish()
            }
            R.id.btnNextPage -> {
                startActivity(StrategySuperbLackActivity.getActivityIntent(this, mStyleType))
                finish()
            }
        }
    }
}