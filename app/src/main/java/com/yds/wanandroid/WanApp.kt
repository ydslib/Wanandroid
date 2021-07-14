package com.yds.wanandroid

import android.app.Application
import com.yds.base.net.RetrofitClient
import com.yds.base.ui.BaseApplication
import com.yds.base.util.Utils

class WanApp : BaseApplication() {

    override fun onCreate() {
        super.onCreate()
        Utils.setUp(this)
        RetrofitClient.setUp(BuildConfig.HOST, arrayListOf())
    }
}