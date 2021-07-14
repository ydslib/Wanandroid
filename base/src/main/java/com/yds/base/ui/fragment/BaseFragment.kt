package com.yds.base.ui.fragment

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yds.base.ui.BaseApplication
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<VM : ViewModel> : Fragment() {

    var mContext: Context? = null

    var mFragmentProvider: ViewModelProvider? = null
    var mActivityProvider: ViewModelProvider? = null
    var mApplicationProvider: ViewModelProvider? = null

    //是否第一次加载
    private var isFirst: Boolean = true

    val mViewModel: VM by lazy {
        ViewModelProvider(this).get(getViewModelClass())
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(savedInstanceState)
        createObserver()
        onVisible()
        loadDataAfterCreate()

    }

    open fun loadDataAfterCreate() {

    }

    private fun onVisible() {
        if (lifecycle.currentState == Lifecycle.State.STARTED && isFirst) {
            lazyLoadData()
            isFirst = false
        }
    }

    /**
     * 初始化view
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 创建观察者
     */
    abstract fun createObserver()

    /**
     * 懒加载
     */
    abstract fun lazyLoadData()

    override fun onResume() {
        super.onResume()
        onVisible()
    }


    //TODO tip 1: DataBinding 严格模式（详见 DataBindingFragment - - - - - ）：
    // 将 DataBinding 实例限制于 base 页面中，默认不向子类暴露，
    // 通过这样的方式，来彻底解决 视图调用的一致性问题，
    // 如此，视图调用的安全性将和基于函数式编程思想的 Jetpack Compose 持平。

    // 如果这样说还不理解的话，详见 https://xiaozhuanlan.com/topic/9816742350 和 https://xiaozhuanlan.com/topic/2356748910

    //TODO tip 2: Jetpack 通过 "工厂模式" 来实现 ViewModel 的作用域可控，
    //目前我们在项目中提供了 Application、Activity、Fragment 三个级别的作用域，
    //值得注意的是，通过不同作用域的 Provider 获得的 ViewModel 实例不是同一个，
    //所以如果 ViewModel 对状态信息的保留不符合预期，可以从这个角度出发去排查 是否眼前的 ViewModel 实例不是目标实例所致。

    //如果这样说还不理解的话，详见 https://xiaozhuanlan.com/topic/6257931840

    //TODO tip 1: DataBinding 严格模式（详见 DataBindingFragment - - - - - ）：
    // 将 DataBinding 实例限制于 base 页面中，默认不向子类暴露，
    // 通过这样的方式，来彻底解决 视图调用的一致性问题，
    // 如此，视图调用的安全性将和基于函数式编程思想的 Jetpack Compose 持平。
    // 如果这样说还不理解的话，详见 https://xiaozhuanlan.com/topic/9816742350 和 https://xiaozhuanlan.com/topic/2356748910
    //TODO tip 2: Jetpack 通过 "工厂模式" 来实现 ViewModel 的作用域可控，
    //目前我们在项目中提供了 Application、Activity、Fragment 三个级别的作用域，
    //值得注意的是，通过不同作用域的 Provider 获得的 ViewModel 实例不是同一个，
    //所以如果 ViewModel 对状态信息的保留不符合预期，可以从这个角度出发去排查 是否眼前的 ViewModel 实例不是目标实例所致。
    //如果这样说还不理解的话，详见 https://xiaozhuanlan.com/topic/6257931840

    fun <T : ViewModel?> getFragmentScopeViewModel(modelClass: Class<T>): T {
        if (mFragmentProvider == null) {
            mFragmentProvider = ViewModelProvider(this)
        }
        return mFragmentProvider!![modelClass]
    }

    fun <T : ViewModel?> getActivityScopeViewModel(modelClass: Class<T>): T {
        if (mActivityProvider == null) {
            mActivityProvider = ViewModelProvider(requireActivity())
        }
        return mActivityProvider!![modelClass]
    }

    fun <T : ViewModel?> getApplicationScopeViewModel(modelClass: Class<T>): T {
        if (mApplicationProvider == null) {
            mApplicationProvider =
                ViewModelProvider(
                    (context?.applicationContext as BaseApplication),
                    getApplicationFactory(requireActivity())
                )
        }
        return mApplicationProvider!![modelClass]
    }

    open fun getApplicationFactory(activity: Activity): ViewModelProvider.Factory {
        checkActivity(this)
        val application: Application = checkApplication(activity)
        return ViewModelProvider.AndroidViewModelFactory.getInstance(application)

    }

    open fun checkApplication(activity: Activity): Application {
        return activity.application
            ?: throw IllegalStateException(
                "Your activity/fragment is not yet attached to "
                        + "Application. You can't request ViewModel before onCreate call."
            )
    }

    open fun checkActivity(fragment: Fragment) {
        fragment.activity
            ?: throw IllegalStateException("Can't create ViewModelProvider for detached fragment")
    }

}