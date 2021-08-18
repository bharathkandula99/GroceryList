package com.tilicho.grocery.mangement.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.*
import com.tilicho.grocery.mangement.api.FirebaseDataManager
import com.tilicho.grocery.mangement.sharedPreferences.AppPreff
import com.tilicho.grocery.mangement.utils.ListsModel
import com.tilicho.grocery.mangement.utils.UserModel
import kotlinx.coroutines.launch

class ListsViewModel(application: Application) : AndroidViewModel(application) {
    var mDatabase: DatabaseReference? = null


    private val _shouldShowEmptyUI: MutableLiveData<Boolean> = MutableLiveData(true)
    val shouldShowEmptyUI: LiveData<Boolean>
        get() = _shouldShowEmptyUI

    private val _lists: MutableLiveData<HashMap<String, ListsModel>> = MutableLiveData()
    val lists: LiveData<HashMap<String, ListsModel>>
        get() = _lists

    fun setUIState(shouldShowEmptyUI: Boolean) {
        _shouldShowEmptyUI.value = shouldShowEmptyUI
    }


    private val _shouldShowGroceryListEmptyUI: MutableLiveData<Boolean> = MutableLiveData(true)
    val shouldShowGroceryListEmptyUI: LiveData<Boolean>
        get() = _shouldShowGroceryListEmptyUI

    init {
        mDatabase = FirebaseDatabase.getInstance().getReference("list")
            .child(AppPreff(application).getUserID().toString())
        addValueEventListeners()
    }

    private fun addValueEventListeners() {
        mDatabase?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

     fun createList() {
         viewModelScope.launch {
             FirebaseDataManager.getInstance().createList("",ListsModel())
         }
    }


    fun setGroceryListUIState(shouldShowGroceryListEmptyUI: Boolean) {
        _shouldShowGroceryListEmptyUI.value = shouldShowGroceryListEmptyUI
    }

    fun createList(listID: String, listsModel: ListsModel) {
        mDatabase?.child(listID)?.setValue(listsModel)
    }


}