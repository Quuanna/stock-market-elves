package com.mitake.finacialidea

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mitake.api.*
import com.mitake.finacialidea.databinding.ActivityWelcomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WelcomeActivity : AppCompatActivity() {

    lateinit var mBinding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.tvStatus.text = "SDK 註冊中"
    }

    override fun onStart() {
        super.onStart()

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.READ_PHONE_STATE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            doInit()
        }
    }

    private fun doInit() {
        //請在此Init方法中填入E-mail及體驗序號
        MitakeFinance.init(
            this,
            "quewenan@mitake.com.tw",
            "beb607ff45daab2cf5b021d49805348b",
            object :
                IInitListener {
                override fun onResult(code: String?, message: String?) {
                    if (code == MitakeFinance.SUCCESS) {
                        CoroutineScope(Dispatchers.Main).launch {
                            Log.d("doInit onResult: ", "$code:$message")
                            MitakeFinance.getInstance().addConnectListener(netWorkListener)
                            //初始化成功後才可以使用登入功能
                            mBinding.tvStatus.text = "SDK 初始化成功"
                            doLogin()
                        }
                    } else {
                        if (!isFinishing) {
                            mBinding.tvStatus.text = "$code:$message"
                            Log.d("doInit error: ", "$code:$message")

                            runOnUiThread {
                                MaterialAlertDialogBuilder(this@WelcomeActivity)
                                    .setMessage(message)
                                    .setNegativeButton("重新連線") { dialog, _ ->
                                        doInit()
                                        dialog.dismiss()
                                    }.show()
                            }
                        }
                    }
                }

                override fun event(m: Message?) {

                }
            })
    }

    private fun doLogin() {
        MitakeFinance.getInstance().login(this, object : ILoginListener {
            override fun onResult(code: String?, message: String?, obj: Any?) {

                CoroutineScope(Dispatchers.Main).launch {
                    if (code == MitakeFinance.SUCCESS) {
                        mBinding.tvStatus.text = "SDK 登入成功"
                        delay(500)

                        val intent = Intent(this@WelcomeActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        this@WelcomeActivity.finish()

                    } else {
                        mBinding.tvStatus.text = "SDK 登入失敗"
                        Log.d("doInit onResult:", "SDK登入失敗")
                    }
                }
            }

            override fun event(m: Message?) {

            }
        })
    }


    private val netWorkListener = object : IConnectListener {
        override fun onConnectChanged(coonectStatus: Int) {
            when (coonectStatus) {
                ConnectStatus.CONNECTED -> {
                    //SDK已重新連線
                    Log.e("onConnectChanged", "CONNECTED")
                }

                ConnectStatus.DISCONNECT -> {
                    //SDK斷線(重連中)
                    Log.e("onConnectChanged", "DISCONNECT")
                }

                ConnectStatus.CHECK_CONNECT -> {
                    //SDK長時間連線不上,讓使用者決定是否繼續重新連線
                    Log.e("onConnectChanged", "CHECK_CONNECT")
                }
            }
        }

        override fun onTokenInvalidate(code: String?, message: String?) {
            Log.e("onTokenInvalidate", "token失效:$code($message)")
        }
    }
}