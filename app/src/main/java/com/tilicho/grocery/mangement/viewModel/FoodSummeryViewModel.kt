package com.tilicho.grocery.mangement.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.*
import com.tilicho.grocery.mangement.api.FirebaseDataManager
import com.tilicho.grocery.mangement.sharedPreferences.AppPreff
import com.tilicho.grocery.mangement.utils.FoodWastageModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class FoodSummeryViewModel(application: Application) : AndroidViewModel(application) {
    private val appViewModelStore by lazy { AppViewModelStore.getInstance(application) }
    private val listsViewModel by lazy { appViewModelStore.getAndroidViewModel<ListsViewModel>() }
    var mDatabase: FirebaseDatabase? = null
    var isCreateWastageListSuccess: ((Boolean, String) -> Unit)? = null


    init {
        viewModelScope.launch {
            mDatabase = FirebaseDatabase.getInstance()
            //.child(AppPreff(application).getUserID().toString())
            addValueEventListeners()
        }
    }

    private suspend fun addValueEventListeners() {
        coroutineScope {
            mDatabase?.getReference("wastage")
                ?.child(AppPreff(getApplication()).getUserID().toString())
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val t = object :
                            GenericTypeIndicator<HashMap<String, FoodWastageModel>>() {}
                        _wastages.value = snapshot.getValue(t)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(
                            getApplication(),
                            error.message.toString(),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                })
        }

    }

    fun addWastageEntry(foodWastageModel: FoodWastageModel) {

        viewModelScope.launch {

            val firebaseDataManager = FirebaseDataManager.getInstance()
            firebaseDataManager.apply {
                dataCreatedHandler = { it ->

                    if (it.isSuccessful) {
                        isCreateWastageListSuccess?.invoke(
                            true,
                            it.exception?.localizedMessage ?: it.exception?.message ?: ""
                        )
                    } else {
                        isCreateWastageListSuccess?.invoke(
                            false,
                            it.exception?.localizedMessage ?: it.exception?.message ?: ""
                        )
                    }
                }
                errorHandler = {
                    isCreateWastageListSuccess?.invoke(
                        false,
                        it?.localizedMessage ?: it?.message ?: ""
                    )

                }
            }
            firebaseDataManager.createWastageEntry(foodWastageModel)
//
        }
    }

    private val _shouldShowFoodSumeryEmptyUI: MutableLiveData<Boolean> = MutableLiveData(true)
    val shouldShowFoodSumeryEmptyUI: LiveData<Boolean>
        get() = _shouldShowFoodSumeryEmptyUI

    private val _wastages: MutableLiveData<HashMap<String, FoodWastageModel>> =
        MutableLiveData()
    val wastages: LiveData<HashMap<String, FoodWastageModel>>
        get() = _wastages

    fun setUIState(shouldShowFoodSumeryEmptyUI: Boolean) {
        _shouldShowFoodSumeryEmptyUI.value = shouldShowFoodSumeryEmptyUI
    }


}