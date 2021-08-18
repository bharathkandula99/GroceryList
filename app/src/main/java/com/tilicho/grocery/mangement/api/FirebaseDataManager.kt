package com.tilicho.grocery.mangement.api

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.tilicho.grocery.mangement.utils.ListsModel
import kotlinx.coroutines.coroutineScope

class FirebaseDataManager {



    /*init {
        mDatabase = FirebaseDatabase.getInstance().getReference("users")
    }*/


    companion object {
        var mDatabase: DatabaseReference? = null
        private var dataManager: FirebaseDataManager? = null

        fun getInstance(): FirebaseDataManager {
            if (dataManager == null) {
                dataManager = FirebaseDataManager()
                mDatabase = FirebaseDatabase.getInstance().getReference("users")
            }
            return dataManager as FirebaseDataManager
        }
    }

    suspend fun createList(listID: String, listsModel: ListsModel) {
        coroutineScope {
            mDatabase?.child(listID)?.setValue(listsModel)?.addOnCompleteListener {

               // it.result?.let { it1 -> dataChangeHandler?.invoke(it.isSuccessful, it1,it.exception.localizedMessage) }

            }?.addOnFailureListener {

                dataChangeHandler?.invoke(it)

            }
        }
    }
}