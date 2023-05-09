package com.tilicho.grocery.mangement.api

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.tilicho.grocery.mangement.utils.*
import kotlinx.coroutines.coroutineScope

class FirebaseDataManager {


    var dataCreatedHandler: ((Task<Void>) -> Unit)? = null
    var dataReadHandler: ((Task<DataSnapshot>) -> Unit)? = null
    var errorHandler: ((Exception) -> Unit)? = null

    companion object {
        var mDatabase: FirebaseDatabase? = null
        private var dataManager: FirebaseDataManager? = null

        fun getInstance(): FirebaseDataManager {
            if (dataManager == null) {
                dataManager = FirebaseDataManager()
                mDatabase = FirebaseDatabase.getInstance()
            }
            return dataManager as FirebaseDataManager
        }
    }

    suspend fun createList(
        listsModel: ListsModel,
    ) {
        coroutineScope {
            mDatabase?.getReference("lists")?.child(listsModel.userId)?.child(listsModel.id)
                ?.setValue(listsModel)?.addOnCompleteListener {
                    dataCreatedHandler?.invoke(it)
                }?.addOnFailureListener {
                    errorHandler?.invoke(it)
                }
        }
    }

    suspend fun createWastageEntry(
        foodWastageModel: FoodWastageModel,
    ) {
        coroutineScope {
            mDatabase?.getReference("wastage")?.child(foodWastageModel.userId)
                ?.child(foodWastageModel.id)
                ?.setValue(foodWastageModel)?.addOnCompleteListener {
                    dataCreatedHandler?.invoke(it)
                }?.addOnFailureListener {
                    errorHandler?.invoke(it)
                }
        }
    }

    suspend fun createCategory(
        categoryModel: CategoryModel
    ) {
        coroutineScope {

            mDatabase?.getReference("categories")?.child(categoryModel.userId)
                ?.child(categoryModel.id)
                ?.setValue(categoryModel)?.addOnCompleteListener {
                    dataCreatedHandler?.invoke(it)
                }?.addOnFailureListener {
                    errorHandler?.invoke(it)
                }
        }
    }

    suspend fun createListItem(newListItem: ListItemModel) {
        coroutineScope {
            mDatabase?.getReference("listItems")?.child(newListItem.userId)
                ?.child(newListItem.listId)
                ?.child(newListItem.id)
                ?.setValue(newListItem)
                ?.addOnCompleteListener {
                    dataCreatedHandler?.invoke(it)
                }?.addOnFailureListener {
                    errorHandler?.invoke(it)
                }
            updateListUpdatedTime(newListItem.userId, newListItem.listId)
        }
    }

    suspend fun createNewItem(itemModel: ItemModel) {
        coroutineScope {
            mDatabase?.getReference("items")?.child(itemModel.userId)?.child(itemModel.id)
                ?.setValue(itemModel)
        }
    }


    suspend fun updateListItem(listItem: ListItemModel) {
        coroutineScope {
            mDatabase?.getReference("listItems")?.child(listItem.userId)
                ?.child(listItem.listId)
                ?.child(listItem.id)
                ?.setValue(listItem)
                ?.addOnCompleteListener {
                    dataCreatedHandler?.invoke(it)
                }?.addOnFailureListener {
                    errorHandler?.invoke(it)
                }
            updateListUpdatedTime(listItem.userId, listItem.listId)
        }
    }


    private fun updateListUpdatedTime(
        userId: String,
        listId: String,
    ) {
        try {
            val updates: MutableMap<String, Any> = HashMap()
            updates["updatedAt"] = System.currentTimeMillis()
            FirebaseDataManager.mDatabase?.getReference("lists")?.child(userId)
                ?.child(listId)?.updateChildren(updates)
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    suspend fun createNewPurchaseItems(
        purchaseHistoryModel: MutableMap<String, Any>,
        userId: String
    ) {
        coroutineScope {
            mDatabase?.getReference("purchaseHistory")?.child(userId)
                ?.updateChildren(purchaseHistoryModel)
        }
    }

    suspend fun updatePurchaseStatus(
        userId: String,
        listId: String,
        updates: MutableMap<String, Any>
    ) {
        mDatabase?.getReference("listItems")?.child(userId)
            ?.child(listId)?.updateChildren(updates)?.addOnCompleteListener {
                dataCreatedHandler?.invoke(it)
            }?.addOnFailureListener {
                errorHandler?.invoke(it)
            }
        updateListUpdatedTime(userId, listId)
    }

    suspend fun createNewItemConsumption(consumptions: MutableMap<String, Any>, userId: String) {
        coroutineScope {
            mDatabase?.getReference("itemConsumptions")?.child(userId)
                ?.updateChildren(consumptions)
        }
    }

    fun updateInventoryDetails(updates: MutableMap<String, Any>) {
        try {
            mDatabase?.reference?.updateChildren(updates)?.addOnCompleteListener {
                dataCreatedHandler?.invoke(it)
            }?.addOnFailureListener {
                errorHandler?.invoke(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            errorHandler?.invoke(e)
        }

    }
}
