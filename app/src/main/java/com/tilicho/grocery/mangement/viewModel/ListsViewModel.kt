package com.tilicho.grocery.mangement.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ListsViewModel(application: Application) : AndroidViewModel(application) {

    private val _shouldShowEmptyUI: MutableLiveData<Boolean> = MutableLiveData(true)
    val shouldShowEmptyUI: LiveData<Boolean>
        get() = _shouldShowEmptyUI

    fun setUIState(shouldShowEmptyUI:Boolean){
        _shouldShowEmptyUI.value = shouldShowEmptyUI
    }


 private val _shouldShowGroceryListEmptyUI: MutableLiveData<Boolean> = MutableLiveData(true)
    val shouldShowGroceryListEmptyUI: LiveData<Boolean>
        get() = _shouldShowGroceryListEmptyUI

    fun setGroceryListUIState(shouldShowGroceryListEmptyUI:Boolean){
        _shouldShowGroceryListEmptyUI.value = shouldShowGroceryListEmptyUI
    }


}