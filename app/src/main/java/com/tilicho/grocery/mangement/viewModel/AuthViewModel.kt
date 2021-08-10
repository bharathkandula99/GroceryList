package com.tilicho.grocery.mangement.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val _loginUIShown: MutableLiveData<Boolean> = MutableLiveData(true)
    val loginUIShown: LiveData<Boolean>
        get() = _loginUIShown

    fun setUIState(loginUIShown:Boolean = true){
        _loginUIShown.value = loginUIShown
    }

}