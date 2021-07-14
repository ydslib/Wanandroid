package com.yds.base.ui.activity

import android.os.Bundle
import android.util.SparseArray
import androidx.core.util.forEach
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.yds.base.ui.DataBindingConfig

abstract class DataBindingActivity<D : ViewDataBinding, VM : ViewModel> : BaseActivity<VM>() {

    protected val dataBindingConfig: DataBindingConfig by lazy {
        initDataBindConfig()
    }

    abstract fun initDataBindConfig(): DataBindingConfig

    lateinit var mBinding: D

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initOtherVM()

        val binding: ViewDataBinding =
            DataBindingUtil.setContentView(this, dataBindingConfig.layout)

        binding.lifecycleOwner = this
        binding.setVariable(
            dataBindingConfig.vmVariableId,
            dataBindingConfig.stateViewModel
        )

        val bindingParams: SparseArray<Any> = dataBindingConfig.bindingParams
        bindingParams.forEach { key, value ->
            binding.setVariable(key, value)
        }

        mBinding = binding as D

        initObserve()

        initData()

    }

    //初始化其他的viewmodel
    open fun initOtherVM() {

    }

    //oncreate后处理数据
    open fun initData() {

    }

    //初始化viewmodel中数据
    open fun initObserve() {

    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
        //  mBinding = null
    }
}