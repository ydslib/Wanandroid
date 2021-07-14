package com.yds.base.ui.activity

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ktx.hasNavigationBar
import com.gyf.immersionbar.ktx.hideStatusBar
import com.gyf.immersionbar.ktx.immersionBar
import com.gyf.immersionbar.ktx.showStatusBar
import com.yds.base.ui.BaseApplication
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<VM : ViewModel> : AppCompatActivity() {

    val mViewMode: VM by lazy {
        ViewModelProvider(this).get(getViewModelClass())
    }

    private var mActivityProvider: ViewModelProvider? = null
    private var mApplicationProvider: ViewModelProvider? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fullScreen()


    }

    private fun getViewModelClass(): Class<VM> {
        val actualTypeArguments =
            (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments

        val filter = actualTypeArguments.filter {
            isVMClass(it as Class<*>)
        }

        return filter[0] as Class<VM>
    }

    fun isVMClass(type: Class<*>): Boolean {
        return if (type.superclass != null) {
            if (type.superclass?.name == "com.yds.base.ui.vm.BaseViewModel" || type.superclass?.name == "androidx.lifecycle.ViewModel") {
                true
            } else {
                isVMClass(type.superclass!!)
            }
        } else {
            false
        }
    }

    private fun fullScreen() {
        immersionBar {
            if (needShowStatus()) {
                showStatusBar()
                statusBarDarkFont(darkStatus())
                navigationBarColor("#FFFFFF")
                navigationBarDarkIcon(darkStatus())
            } else {
                hideStatusBar()
                fullScreen(true)
            }
            if (hasNavigationBar && isHideBar()) {
                hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
            }
        }
    }

    /**
     * 是否隐藏导航栏
     */
    open fun isHideBar(): Boolean {
        return true
    }

    open fun darkStatus() = true

    open fun needShowStatus() = true

    //TODO tip 2: Jetpack 通过 "工厂模式" 来实现 ViewModel 的作用域可控，
    //目前我们在项目中提供了 Application、Activity、Fragment 三个级别的作用域，
    //值得注意的是，通过不同作用域的 Provider 获得的 ViewModel 实例不是同一个，
    //所以如果 ViewModel 对状态信息的保留不符合预期，可以从这个角度出发去排查 是否眼前的 ViewModel 实例不是目标实例所致。

    //如果这样说还不理解的话，详见 https://xiaozhuanlan.com/topic/6257931840

    //TODO tip 2: Jetpack 通过 "工厂模式" 来实现 ViewModel 的作用域可控，
    //目前我们在项目中提供了 Application、Activity、Fragment 三个级别的作用域，
    //值得注意的是，通过不同作用域的 Provider 获得的 ViewModel 实例不是同一个，
    //所以如果 ViewModel 对状态信息的保留不符合预期，可以从这个角度出发去排查 是否眼前的 ViewModel 实例不是目标实例所致。
    //如果这样说还不理解的话，详见 https://xiaozhuanlan.com/topic/6257931840

    protected open fun <T : ViewModel?> getActivityScopeViewModel(modelClass: Class<T>): T {
        if (mActivityProvider == null) {
            mActivityProvider = ViewModelProvider(this)
        }
        return mActivityProvider!![modelClass]
    }

    protected open fun <T : ViewModel?> getApplicationScopeViewModel(modelClass: Class<T>): T {
        if (mApplicationProvider == null) {
            mApplicationProvider =
                ViewModelProvider(applicationContext as BaseApplication, getAppFactory(this))
        }
        return mApplicationProvider!![modelClass]
    }

    private fun getAppFactory(activity: Activity): ViewModelProvider.Factory {
        val application = checkApplication(activity)
        return ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    private fun checkApplication(activity: Activity): Application {
        return activity.application
            ?: throw IllegalStateException(
                "Your activity/fragment is not yet attached to "
                        + "Application. You can't request ViewModel before onCreate call."
            )
    }
}