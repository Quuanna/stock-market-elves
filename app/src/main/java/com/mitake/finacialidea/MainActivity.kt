package com.mitake.finacialidea

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mitake.finacialidea.adapter.CardPagerAdapter
import com.mitake.finacialidea.data.CardItem
import com.mitake.finacialidea.data.ObjectType
import com.mitake.finacialidea.databinding.ActivityCardMainBinding
import com.mitake.finacialidea.viewpagercards.ShadowTransformer

class MainActivity : AppCompatActivity(), CardPagerAdapter.SelectResultListener {


    private var mCardAdapter: CardPagerAdapter? = null
    private var mCardShadowTransformer: ShadowTransformer? = null
    private lateinit var mBinding: ActivityCardMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityCardMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initCard()
    }

    private fun initCard() {
        mCardAdapter = CardPagerAdapter(this)
        setItem()
        mCardShadowTransformer = ShadowTransformer(mBinding.viewPager, mCardAdapter)
        mCardShadowTransformer?.enableScaling(true) // 縮放
        mBinding.viewPager.adapter = mCardAdapter
        mBinding.viewPager.setPageTransformer(false, mCardShadowTransformer)
        mBinding.viewPager.offscreenPageLimit = 3

        mBinding.viewPager.adapter = mCardAdapter
        mBinding.viewPager.setPageTransformer(false, mCardShadowTransformer)
    }

    private fun setItem() {
        mCardAdapter?.addCardItem(
            CardItem(
                R.string.title_1,
                R.string.text_1_profit,
                R.string.text_1_target,
                R.string.text_1_have_skills,
                ObjectType.SHORT_TERM
            )
        )
        mCardAdapter?.addCardItem(
            CardItem(
                R.string.title_2,
                R.string.text_2_profit,
                R.string.text_2_target,
                R.string.text_2_have_skills,
                ObjectType.VALUE_TYPE
            )
        )
        mCardAdapter?.addCardItem(
            CardItem(
                R.string.title_3,
                R.string.text_3_profit,
                R.string.text_3_target,
                R.string.text_3_have_skills,
                ObjectType.GROWTH_TYPE
            )
        )
        mCardAdapter?.addCardItem(
            CardItem(
                R.string.title_4,
                R.string.text_4_profit,
                R.string.text_4_target,
                R.string.text_4_have_skills,
                ObjectType.STABLE_INVESTMENT
            )
        )
    }

    override fun onUserSelectResult(type: ObjectType) {

        startActivity(InformationActivity.getActivityIntent(this, type))

    }

}