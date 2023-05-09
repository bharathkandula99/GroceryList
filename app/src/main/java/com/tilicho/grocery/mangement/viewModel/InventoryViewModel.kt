package com.tilicho.grocery.mangement.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class InventoryViewModel (application: Application) : AndroidViewModel(application) {

    private val _shouldShowEmptyUI: MutableLiveData<Boolean> = MutableLiveData(true)
    val shouldShowEmptyUI: LiveData<Boolean>
        get() = _shouldShowEmptyUI

    fun setUIState(shouldShowEmptyUI:Boolean){
        _shouldShowEmptyUI.value = shouldShowEmptyUI
    }


}