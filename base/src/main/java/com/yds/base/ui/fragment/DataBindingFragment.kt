package com.yds.base.ui.fragment

import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.yds.base.ui.DataBindingConfig

abstract class DataBindingFragment<D : ViewDataBinding, VM : ViewModel> : BaseFragment<VM>() {

    var mBinding: D? = null

    protected val dataBindingConfig: DataBindingConfig by lazy {
        initDataBindConfig()
    }

    abstract fun initDataBindConfig(): DataBindingConfig


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initOtherVM()

        val binding: ViewDataBinding =
            DataBindingUtil.inflate(inflater, dataBindingConfig.layout, container, false)
        binding.lifecycleOwner = this
        binding.setVariable(dataBindingConfig.vmVariableId, dataBindingConfig.stateViewModel)

        val bindingParams: SparseArray<Any> = dataBindingConfig.bindingParams
        bindingParams.forEach { key, value ->
            binding.setVariable(key, value)
        }

        mBinding = binding as D

        return binding.root
    }

    //初始化其他的viewmodel
    open fun initOtherVM() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mBinding?.unbind()
        mBinding = null
    }

}