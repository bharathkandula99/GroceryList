package com.tilicho.grocery.mangement.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class FoodSummeryViewModel(application: Application) : AndroidViewModel(application) {

    private val _shouldShowFoodSumeryEmptyUI: MutableLiveData<Boolean> = MutableLiveData(true)
    val shouldShowFoodSumeryEmptyUI: LiveData<Boolean>
        get() = _shouldShowFoodSumeryEmptyUI

    fun setUIState(shouldShowFoodSumeryEmptyUI:Boolean){
        _shouldShowFoodSumeryEmptyUI.value = shouldShowFoodSumeryEmptyUI
    }
}