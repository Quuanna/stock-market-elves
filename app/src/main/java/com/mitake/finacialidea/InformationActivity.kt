package com.mitake.finacialidea

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mitake.finacialidea.data.ObjectType
import com.mitake.finacialidea.databinding.ActivityInformationBinding

class InformationActivity : AppCompatActivity() {

    companion object {
        private const val STYLE_TYPE = "STYLE_TYPE"
        fun getActivityIntent(context: Context, type: ObjectType): Intent {
            return Intent(context, InformationActivity::class.java)
                .putExtra(STYLE_TYPE, type)
        }
    }

    private lateinit var mBinding: ActivityInformationBinding
    private val mStyleType: ObjectType by lazy { initStyleType() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityInformationBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        initView()
    }

    private fun initView() {
        when (mStyleType) {
            ObjectType.SHORT_TERM -> {

            }
            ObjectType.VALUE_TYPE -> {

            }
            ObjectType.GROWTH_TYPE -> {

            }
            ObjectType.STABLE_INVESTMENT -> {

            }
        }
    }

    private fun initStyleType(): ObjectType {
        return intent.getSerializableExtra(STYLE_TYPE) as ObjectType
    }
}
