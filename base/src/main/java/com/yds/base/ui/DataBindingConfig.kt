package com.yds.base.ui

import android.util.SparseArray
import androidx.annotation.LayoutRes
import androidx.lifecycle.ViewModel

class DataBindingConfig(
    @LayoutRes val layout: Int = 0,
    val vmVariableId: Int = 0,
    val stateViewModel: ViewModel? = null
) {
    val bindingParams: SparseArray<Any> = SparseArray<Any>()

    fun addBindingParam(variableId:Int,any:Any):DataBindingConfig{
        bindingParams[variableId]?:bindingParams.put(variableId,any)
        return this
    }
}