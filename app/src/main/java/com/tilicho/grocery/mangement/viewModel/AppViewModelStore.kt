package com.tilicho.grocery.mangement.viewModel

import android.app.Application
import androidx.lifecycle.*

class AppViewModelStore
private constructor(val application: Application) : ViewModelStoreOwner {

    private val store: ViewModelStore by lazy { ViewModelStore() }

    companion object {

        private var INSTANCE: AppViewModelStore? = null

        fun getInstance(application: Application): AppViewModelStore {
            var tempInstance = INSTANCE
            if (tempInstance == null) {
                tempInstance = AppViewModelStore(application)
                INSTANCE = tempInstance
            }
            return tempInstance
        }
    }

    override fun getViewModelStore(): ViewModelStore {
        return store
    }

    inline fun <reified T : ViewModel> getViewModel(): T {
        return ViewModelProvider(this).get(T::class.java)
    }

    inline fun <reified T : ViewModel> getViewModel(viewModelFactory: ViewModelProvider.Factory): T {
        return ViewModelProvider(this, viewModelFactory).get(T::class.java)
    }

    inline fun <reified T : AndroidViewModel> getAndroidViewModel(): T {
        return ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory(application)
        ).get(T::class.java)
    }

}