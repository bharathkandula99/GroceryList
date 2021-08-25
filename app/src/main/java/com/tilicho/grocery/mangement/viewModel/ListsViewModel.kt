package com.tilicho.grocery.mangement.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.google.firebase.database.*
import com.tilicho.grocery.mangement.api.FirebaseDataManager
import com.tilicho.grocery.mangement.sharedPreferences.AppPreff
import com.tilicho.grocery.mangement.utils.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class ListsViewModel(application: Application) : AndroidViewModel(application) {
    var mDatabase: FirebaseDatabase? = null
    var isCreateListSucess: ((Boolean, String) -> Unit)? = null
    var isCreateCategorySucess: ((Boolean, String) -> Unit)? = null
    var isCreateListItemSucess: ((Boolean, String) -> Unit)? = null
    var isPurchaseSuccess: ((Boolean, String) -> Unit)? = null
    var isInventoryDetailsUpdationSuccess: ((Boolean, String) -> Unit)? = null


    private val _shouldShowEmptyUI: MutableLiveData<Boolean> = MutableLiveData(true)
    val shouldShowEmptyUI: LiveData<Boolean>
        get() = _shouldShowEmptyUI

    private val _lists: MutableLiveData<HashMap<String, ListsModel>> = MutableLiveData()
    val lists: LiveData<HashMap<String, ListsModel>>
        get() = _lists

    val _currentLists: MutableLiveData<HashMap<String, ListsModel>> = MutableLiveData()
    val currentLists: LiveData<HashMap<String, ListsModel>>
        get() = _currentLists

    val _historyLists: MutableLiveData<HashMap<String, ListsModel>> = MutableLiveData()
    val historyLists: LiveData<HashMap<String, ListsModel>>
        get() = _historyLists

    private val _categories: MutableLiveData<HashMap<String, CategoryModel>> = MutableLiveData()
    val categories: LiveData<HashMap<String, CategoryModel>>
        get() = _categories

    private val _selectedList: MutableLiveData<ListsModel> = MutableLiveData()
    val selectedList: LiveData<ListsModel>
        get() = _selectedList

    private val _selectedListItem: MutableLiveData<ListItemModel> = MutableLiveData()
    val selectedListItem: LiveData<ListItemModel>
        get() = _selectedListItem

    private val _units: MutableLiveData<HashMap<String, UnitModel>> = MutableLiveData()
    val units: LiveData<HashMap<String, UnitModel>>
        get() = _units

    private val _items: MutableLiveData<HashMap<String, ItemModel>> = MutableLiveData()
    val items: LiveData<HashMap<String, ItemModel>>
        get() = _items

    private val _consumptions: MutableLiveData<HashMap<String, HashMap<String, ItemConsumptions>>> =
        MutableLiveData()
    val consumptions: LiveData<HashMap<String, HashMap<String, ItemConsumptions>>>
        get() = _consumptions

    private val _listItems: MutableLiveData<HashMap<String, HashMap<String, ListItemModel>>> =
        MutableLiveData()
    val listItems: LiveData<HashMap<String, HashMap<String, ListItemModel>>>
        get() = _listItems

    private val _purchaseHistory: MutableLiveData<HashMap<String, HashMap<String, HashMap<String, PurchaseHistoryModel>>>> =
        MutableLiveData()
    val purchaseHistory: LiveData<HashMap<String, HashMap<String, HashMap<String, PurchaseHistoryModel>>>>
        get() = _purchaseHistory

    private val _shouldShowGroceryListEmptyUI: MutableLiveData<Boolean> = MutableLiveData(true)
    val shouldShowGroceryListEmptyUI: LiveData<Boolean>
        get() = _shouldShowGroceryListEmptyUI

    private val _shouldShowInventoryListEmptyUI: MutableLiveData<Boolean> = MutableLiveData(true)
    val shouldShowInventoryListEmptyUI: LiveData<Boolean>
        get() = _shouldShowInventoryListEmptyUI

    init {
        viewModelScope.launch {
            val appViewModelStore by lazy { AppViewModelStore.getInstance(application) }
            val foodSummeryViewModel by lazy { appViewModelStore.getAndroidViewModel<FoodSummeryViewModel>() }
            mDatabase = FirebaseDatabase.getInstance()
            //.child(AppPreff(application).getUserID().toString())
            addValueEventListeners()
        }
    }

    fun clear(viewLifecycleOwner: LifecycleOwner) {
        lists.removeObservers(viewLifecycleOwner)
        listItems.removeObservers(viewLifecycleOwner)
        items.removeObservers(viewLifecycleOwner)
        categories.removeObservers(viewLifecycleOwner)
        units.removeObservers(viewLifecycleOwner)
    }

    fun setUIState(shouldShowEmptyUI: Boolean) {
        _shouldShowEmptyUI.value = shouldShowEmptyUI
    }

    fun setGroceryListUIState(shouldShowGroceryListEmptyUI: Boolean) {
        _shouldShowGroceryListEmptyUI.value = shouldShowGroceryListEmptyUI
    }

    fun setInventoryListUIState(shouldShowInventoryListEmptyUI: Boolean) {
        _shouldShowInventoryListEmptyUI.value = shouldShowInventoryListEmptyUI
    }

    fun setSelectedList(selectedList: ListsModel) {
        _selectedList.value = selectedList
    }

    fun setSelectedListItem(selectedListItemModel: ListItemModel) {
        _selectedListItem.value = selectedListItemModel
    }

    fun createList(listsModel: ListsModel) {
        viewModelScope.launch {

            val firebaseDataManager = FirebaseDataManager.getInstance()
            firebaseDataManager.apply {
                dataCreatedHandler = { it ->

                    if (it.isSuccessful) {
                        isCreateListSucess?.invoke(
                            true,
                            it.exception?.localizedMessage ?: it.exception?.message ?: ""
                        )
                    } else {
                        isCreateListSucess?.invoke(
                            false,
                            it.exception?.localizedMessage ?: it.exception?.message ?: ""
                        )
                    }
                }
                errorHandler = {
                    isCreateListSucess?.invoke(false, it?.localizedMessage ?: it?.message ?: "")

                }
            }
            firebaseDataManager.createList(listsModel)
//
        }
    }

    fun createCategory(categoryModel: CategoryModel) {
        viewModelScope.launch {

            val firebaseDataManager = FirebaseDataManager.getInstance()
            firebaseDataManager.apply {
                dataCreatedHandler = { it ->

                    if (it.isSuccessful) {
                        isCreateCategorySucess?.invoke(
                            true,
                            it.exception?.localizedMessage ?: it.exception?.message ?: ""
                        )
                    } else {
                        isCreateListSucess?.invoke(
                            false,
                            it.exception?.localizedMessage ?: it.exception?.message ?: ""
                        )
                    }
                }
                errorHandler = {
                    isCreateListSucess?.invoke(false, it?.localizedMessage ?: it?.message ?: "")

                }
            }
            firebaseDataManager.createCategory(categoryModel)
//
        }
    }

    fun updateInventoryDetails(updates: MutableMap<String, Any>) {
        viewModelScope.launch {

            val firebaseDataManager = FirebaseDataManager.getInstance()
            firebaseDataManager.apply {
                dataCreatedHandler = { it ->

                    if (it.isSuccessful) {
                        isInventoryDetailsUpdationSuccess?.invoke(
                            true,
                            it.exception?.localizedMessage ?: it.exception?.message ?: ""
                        )
                    } else {
                        isInventoryDetailsUpdationSuccess?.invoke(
                            false,
                            it.exception?.localizedMessage ?: it.exception?.message ?: ""
                        )
                    }
                }
                errorHandler = {
                    isInventoryDetailsUpdationSuccess?.invoke(false, it?.localizedMessage ?: it?.message ?: "")

                }
            }
            firebaseDataManager.updateInventoryDetails(updates)
//
        }
    }

    fun markAsAPurchase(
        selectedItemsList: MutableList<ListItemModel>,
        createNewPurchaseHistory: MutableList<PurchaseHistoryModel>,
        createNewItemConsumptions: MutableList<ItemConsumptions>
    ) {
        viewModelScope.launch {
            //prepareHashmapForPurchasedItems(selectedItemsList)
            prepareHashmapForPurchasedItems(
                selectedItemsList,
                createNewPurchaseHistory,
                createNewItemConsumptions
            )
        }
    }

    private suspend fun prepareHashmapForPurchasedItems(
        selectedItemsList: MutableList<ListItemModel>,
        createNewPurchaseHistory: MutableList<PurchaseHistoryModel>,
        createNewItemConsumptions: MutableList<ItemConsumptions>
    ) {
        val firebaseDataManager = FirebaseDataManager.getInstance()
        val updates: MutableMap<String, Any> = HashMap()
        val newPurchaseHistory: MutableMap<String, Any> = HashMap()
        val consumptions: MutableMap<String, Any> = HashMap()
        val listId = selectedItemsList[0].listId
        val userId = selectedItemsList[0].userId

        selectedItemsList.forEach {
            updates["${it.id}/purchased"] = true
            updates["${it.id}/purchasedDate"] = System.currentTimeMillis()
        }
        /*b0a6c268-e603-4557-bd50-bb5208887cd2/7deb8cbd-00f9-4cde-82f4-7d4866e45fc1/47d44505-57ba-4145-a167-26d441ed0f6c*/
        createNewPurchaseHistory.forEach {
            newPurchaseHistory["${it.listId}/${it.itemId}/${it.id}"] = PurchaseHistoryModel(
                it.id,
                it.itemId,
                it.listId,
                it.quantity,
                it.price,
                it.packageSize,
                it.quantity,
                it.createdAt ?: System.currentTimeMillis(),
                it.updatedAt ?: System.currentTimeMillis(),
                it.userId,
                it.itemName,
                it.unitId,
                it.categoryId
            )
        }
        createNewItemConsumptions.forEach {
            consumptions["${it.itemId}/${it.id}"] = ItemConsumptions(
                it.id,
                it.itemId,
                it.quantity,
                it.availableQuantity,
                it.createdAt,
                it.updatedAt,
                it.packageSize,
                it.userId
            )
        }

        firebaseDataManager.apply {
            dataCreatedHandler = { it ->
                if (it.isSuccessful) {
                    isPurchaseSuccess?.invoke(
                        true,
                        it.exception?.localizedMessage ?: it.exception?.message ?: ""
                    )
                } else {
                    isPurchaseSuccess?.invoke(
                        false,
                        it.exception?.localizedMessage ?: it.exception?.message ?: ""
                    )
                }
            }
            errorHandler = {
                isPurchaseSuccess?.invoke(false, it?.localizedMessage ?: it?.message ?: "")

            }
        }

        firebaseDataManager.updatePurchaseStatus(userId, listId, updates)
        firebaseDataManager.createNewPurchaseItems(newPurchaseHistory, userId)
        firebaseDataManager.createNewItemConsumption(consumptions, userId)
    }

    fun createNewListItem(newListItem: ListItemModel, shouldCreateNewItem: Boolean) {
        viewModelScope.launch {
            val firebaseDataManager = FirebaseDataManager.getInstance()
            firebaseDataManager.apply {
                dataCreatedHandler = { it ->

                    if (it.isSuccessful) {
                        isCreateListItemSucess?.invoke(
                            true,
                            it.exception?.localizedMessage ?: it.exception?.message ?: ""
                        )
                    } else {
                        isCreateListItemSucess?.invoke(
                            false,
                            it.exception?.localizedMessage ?: it.exception?.message ?: ""
                        )
                    }
                }
                errorHandler = {
                    isCreateListItemSucess?.invoke(false, it?.localizedMessage ?: it?.message ?: "")

                }
            }
            if (shouldCreateNewItem) {
                firebaseDataManager.createListItem(newListItem)
                firebaseDataManager.createNewItem(
                    ItemModel(
                        newListItem.itemId,
                        newListItem.itemName,
                        newListItem.unitId,
                        newListItem.userId,
                        newListItem.categoryId,
                        0.0
                    )
                )
            } else {
                firebaseDataManager.createListItem(newListItem)
            }
        }
    }

    fun updateListItem(listItem: ListItemModel, shouldCreateNewItem: Boolean) {
        viewModelScope.launch {
            val firebaseDataManager = FirebaseDataManager.getInstance()
            firebaseDataManager.apply {
                dataCreatedHandler = { it ->

                    if (it.isSuccessful) {
                        isCreateListItemSucess?.invoke(
                            true,
                            it.exception?.localizedMessage ?: it.exception?.message ?: ""
                        )
                    } else {
                        isCreateListItemSucess?.invoke(
                            false,
                            it.exception?.localizedMessage ?: it.exception?.message ?: ""
                        )
                    }
                }
                errorHandler = {
                    isCreateListItemSucess?.invoke(false, it?.localizedMessage ?: it?.message ?: "")

                }
            }
            if (shouldCreateNewItem) {
                firebaseDataManager.updateListItem(listItem)
                firebaseDataManager.createNewItem(
                    ItemModel(
                        listItem.itemId,
                        listItem.itemName,
                        listItem.unitId,
                        listItem.userId,
                        listItem.categoryId,
                        0.0
                    )
                )
            } else {
                firebaseDataManager.updateListItem(listItem)
            }
        }
    }

    private suspend fun addValueEventListeners() {

        coroutineScope {

            mDatabase?.getReference("lists")
                ?.child(AppPreff(getApplication()).getUserID().toString())
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val t = object : GenericTypeIndicator<HashMap<String, ListsModel>>() {}
                        _lists.value = snapshot.getValue(t)
                        //= snapshot.value as HashMap<String, ListsModel>
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

            mDatabase?.getReference("listItems")
                ?.child(AppPreff(getApplication()).getUserID().toString())
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val t = object :
                            GenericTypeIndicator<HashMap<String, HashMap<String, ListItemModel>>>() {}
                        _listItems.value = snapshot.getValue(t)
                        //= snapshot.value as HashMap<String, ListsModel>
                        Toast.makeText(getApplication(), "snapshot", Toast.LENGTH_SHORT).show()
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

            mDatabase?.getReference("units")
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val t = object : GenericTypeIndicator<HashMap<String, UnitModel>>() {}
                        _units.value = snapshot.getValue(t)
                        //= snapshot.value as HashMap<String, ListsModel>
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

            mDatabase?.getReference("items")
                ?.child(AppPreff(getApplication()).getUserID().toString())
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val t = object : GenericTypeIndicator<HashMap<String, ItemModel>>() {}
                        _items.value = snapshot.getValue(t)
                        //= snapshot.value as HashMap<String, ListsModel>
                        Toast.makeText(getApplication(), "snapshot", Toast.LENGTH_SHORT).show()
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

            mDatabase?.getReference("categories")
                ?.child(AppPreff(getApplication()).getUserID().toString())
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val t = object : GenericTypeIndicator<HashMap<String, CategoryModel>>() {}
                        _categories.value = snapshot.getValue(t)
                        //= snapshot.value as HashMap<String, ListsModel>
                        Toast.makeText(getApplication(), "snapshot", Toast.LENGTH_SHORT).show()
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

            mDatabase?.getReference("itemConsumptions")
                ?.child(AppPreff(getApplication()).getUserID().toString())
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val t = object :
                            GenericTypeIndicator<HashMap<String, HashMap<String, ItemConsumptions>>>() {}
                        _consumptions.value = snapshot.getValue(t)
                        //= snapshot.value as HashMap<String, ListsModel>
                        Toast.makeText(getApplication(), "snapshot", Toast.LENGTH_SHORT).show()
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

            mDatabase?.getReference("purchaseHistory")
                ?.child(AppPreff(getApplication()).getUserID().toString())
                ?.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val t = object :
                            GenericTypeIndicator<HashMap<String, HashMap<String, HashMap<String, PurchaseHistoryModel>>>>() {}
                        _purchaseHistory.value = snapshot.getValue(t)
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


}