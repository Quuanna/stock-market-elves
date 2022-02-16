package com.mitake.finacialidea

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mitake.finacialidea.data.constant.UserSelectType
import com.mitake.finacialidea.databinding.ActivityStrategySuperbLackBinding

/**
 *  策略優缺點
 */
class StrategySuperbLackActivity : AppCompatActivity(), View.OnClickListener{

    companion object {
        private const val STYLE_TYPE = "STYLE_TYPE"
        fun getActivityIntent(context: Context, type: UserSelectType): Intent {
            return Intent(context, StrategySuperbLackActivity::class.java)
                .putExtra(STYLE_TYPE, type)
        }
    }

    private lateinit var mBinding: ActivityStrategySuperbLackBinding
    private val mStyleType: UserSelectType by lazy { initStyleType() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityStrategySuperbLackBinding.inflate(layoutInflater)
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
                mBinding.tvSuperbContent.text = getString(R.string.strategy_content_short_term_superb)
                mBinding.tvLackContent.text = getString(R.string.strategy_content_short_term_lack)
            }
            UserSelectType.VALUE_TYPE -> {
                mBinding.tvSuperbContent.text = getString(R.string.strategy_content_value_term_superb)
                mBinding.tvLackContent.text = getString(R.string.strategy_content_value_term_lack)
            }
            UserSelectType.GROWTH_TYPE -> {
                mBinding.tvSuperbContent.text = getString(R.string.strategy_content_growth_term_superb)
                mBinding.tvLackContent.text = getString(R.string.strategy_content_growth_term_lack)
            }
            UserSelectType.STABLE_INVESTMENT -> {
                mBinding.tvSuperbContent.text = getString(R.string.strategy_content_sound_investment_superb)
                mBinding.tvLackContent.text = getString(R.string.strategy_content_sound_investment_lack)
            }
        }
    }

    private fun initStyleType(): UserSelectType {
        return intent.getSerializableExtra(STYLE_TYPE) as UserSelectType
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.backLastPage -> {
                startActivity(StrategyActivity.getActivityIntent(this, mStyleType))
                finish()
            }
            R.id.btnNextPage -> {
                startActivity(InformationActivity.getActivityIntent(this, mStyleType))
                finish()
            }
        }
    }
}