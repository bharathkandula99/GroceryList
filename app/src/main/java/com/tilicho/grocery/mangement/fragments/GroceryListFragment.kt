package com.tilicho.grocery.mangement.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.adapter.GroceryListAdapter
import com.tilicho.grocery.mangement.api.FirebaseDataManager
import com.tilicho.grocery.mangement.databinding.FragmentGroceryListBinding
import com.tilicho.grocery.mangement.dialog.AddItemDialog
import com.tilicho.grocery.mangement.dialog.AddPriceDialog
import com.tilicho.grocery.mangement.utils.ItemConsumptions
import com.tilicho.grocery.mangement.utils.ListItemModel
import com.tilicho.grocery.mangement.utils.PurchaseHistoryModel
import com.tilicho.grocery.mangement.viewModel.AppViewModelStore
import com.tilicho.grocery.mangement.viewModel.ListsViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class GroceryListFragment : Fragment() {

    private lateinit var binding: FragmentGroceryListBinding
    private val appViewModelStore by lazy { AppViewModelStore.getInstance(requireActivity().application) }
    private val listsViewModel by lazy { appViewModelStore.getAndroidViewModel<ListsViewModel>() }
    var selectedItemPositions: MutableList<Int> = mutableListOf()
    var listOfNonPurchased: MutableList<ListItemModel> = mutableListOf<ListItemModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_grocery_list, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        //dialog = ProgressDialog(context)
        super.onActivityCreated(savedInstanceState)
        binding.listViewModel = listsViewModel
        populateUI()
        initListeners()
        addObservers()
        // populateGroceryListRecyclerView()
    }

    private fun populateUI() {
        binding.createdOn.text =
            SimpleDateFormat("dd/MM/yyyy").format(listsViewModel.selectedList.value?.createdAt)
                .toString()
    }

    private fun addObservers() {

        listsViewModel.apply {
            isPurchaseSuccess = { b: Boolean, s: String ->
                hideProgressBar()
                if (!b) {
                    Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
                } else {
                   // binding.root.findNavController().navigateUp()
                }
            }
        }


        listsViewModel.shouldShowGroceryListEmptyUI.observe(viewLifecycleOwner) {
            if (it) {
                populateGroceryListRecyclerView()
            }
        }

        listsViewModel.listItems.observe(viewLifecycleOwner) {
            try {
                val listItems =
                    listsViewModel.listItems.value!!.get(listsViewModel.selectedList.value?.id)?.values
                if (listItems != null && listItems.isNotEmpty()) {
                    listOfNonPurchased =
                        listItems.filter { !it.purchased } as MutableList<ListItemModel>
                    if (listOfNonPurchased != null && listOfNonPurchased.isNotEmpty()) {
                        listsViewModel.setGroceryListUIState(false)
                        populateGroceryListRecyclerView()
                    } else {
                        listsViewModel.setGroceryListUIState(true)
                    }
                } else {
                    listsViewModel.setGroceryListUIState(true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(requireContext(), "exception listItems observer", Toast.LENGTH_LONG)
                    .show()
                listsViewModel.setGroceryListUIState(true)
            }
        }
    }

    private fun initListeners() {
        binding.backArrow.setOnClickListener {
            binding.root.findNavController().navigateUp()
        }
        binding.markAsPurchase.setOnClickListener {
            Toast.makeText(requireContext(), "toast", Toast.LENGTH_SHORT).show()
            //performItemPurchase()

            showAddPriceDialog()
        }
        binding.createGroceryListButton.setOnClickListener {
            showAddItemDialog()
        }
        binding.menu.setOnClickListener {
            it.showContextMenu()
        }
        binding.share.setOnClickListener {
            performShareFunctionality()
        }
        binding.floatingActionButton.setOnClickListener {         selectedItemPositions = mutableListOf()
            showAddItemDialog() }
        binding.groceryListTitle.setOnClickListener { listsViewModel.setGroceryListUIState(!listsViewModel.shouldShowGroceryListEmptyUI.value!!) }
    }

    private fun performShareFunctionality() {
        var shareString = "****** Grocery List ****** \n"
        listOfNonPurchased.forEach { it ->
            val unit =
                listsViewModel.units.value?.values?.filter { it1 -> it1.id == it.unitId }
                    ?.get(0)?.name
            shareString =
                shareString + " " + it.itemName + " \t \t \t \t " + "${it.quantity.toInt()} * ${it.packageSize.toInt()} $unit" + "\n "
        }
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        val shareBody = shareString
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Grocery List")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(Intent.createChooser(sharingIntent, "Share via"))
    }

    private fun showAddPriceDialog() {
        val dialog = AddPriceDialog().apply {
            yesButtonClicked = {
                navigateToAddPriceFragment()
                Toast.makeText(requireContext(), "yes", Toast.LENGTH_SHORT).show()
            }
            noButtonClicked = {

                Toast.makeText(requireContext(), "no", Toast.LENGTH_SHORT).show()
                performItemPurchasesNoPrice()
            }
        }
        dialog.show(requireActivity().supportFragmentManager, "create_list_dialog")
    }

    private fun navigateToAddPriceFragment() {
        val selectedListItemsList: MutableList<ListItemModel> = mutableListOf()
        val createNewPurchaseHistory: MutableList<PurchaseHistoryModel> = mutableListOf()
        val createNewItemConsumptions: MutableList<ItemConsumptions> = mutableListOf()
        listOfNonPurchased.forEachIndexed { index, element ->
            if (selectedItemPositions.contains(index)) {
                selectedListItemsList.add(element)
                val purchaseId = UUID.randomUUID().toString()
                val consumptionsId = UUID.randomUUID().toString()
                createNewItemConsumptions.add(
                    ItemConsumptions(
                        consumptionsId,
                        element.itemId,
                        element.quantity,
                        element.quantity,
                        System.currentTimeMillis(),
                        System.currentTimeMillis(),
                        element.packageSize,
                        element.userId
                    )
                )
                createNewPurchaseHistory.add(
                    PurchaseHistoryModel(
                        purchaseId,
                        element.itemId,
                        element.listId,
                        element.quantity,
                        "",
                        element.packageSize,
                        element.quantity,
                        System.currentTimeMillis(),
                        System.currentTimeMillis(),
                        element.userId,
                        element.itemName,
                        element.unitId,
                        element.categoryId
                    )
                )


            }
        }
        selectedItemPositions = mutableListOf()
        val args = bundleOf(
            "selectedListItemsList" to selectedListItemsList,
            "createNewPurchaseHistory" to createNewPurchaseHistory,
            "createNewItemConsumptions" to createNewItemConsumptions
        )
        binding.root.findNavController().navigate(R.id.grocery_list_to_add_price_fragment, args)
        binding.markAsPurchase.visibility = View.GONE
    }

    private fun performItemPurchasesNoPrice() {

        showprogressBar()
        val selectedListItemsList: MutableList<ListItemModel> = mutableListOf()
        val createNewPurchaseHistory: MutableList<PurchaseHistoryModel> = mutableListOf()
        val createNewItemConsumptions: MutableList<ItemConsumptions> = mutableListOf()
        listOfNonPurchased.forEachIndexed { index, element ->
            if (selectedItemPositions.contains(index)) {
                selectedListItemsList.add(element)
                val purchaseId = UUID.randomUUID().toString()
                val consumptionsId = UUID.randomUUID().toString()
                createNewItemConsumptions.add(
                    ItemConsumptions(
                        consumptionsId,
                        element.itemId,
                        element.quantity,
                        element.quantity,
                        System.currentTimeMillis(),
                        System.currentTimeMillis(),
                        element.packageSize,
                        element.userId
                    )
                )
                createNewPurchaseHistory.add(
                    PurchaseHistoryModel(
                        purchaseId,
                        element.itemId,
                        element.listId,
                        element.quantity,
                        "",
                        element.packageSize,
                        element.quantity,
                        System.currentTimeMillis(),
                        System.currentTimeMillis(),
                        element.userId,
                        element.itemName,
                        element.unitId,
                        element.categoryId
                    )
                )
            }
        }
        listsViewModel.markAsAPurchase(
            selectedListItemsList,
            createNewPurchaseHistory,
            createNewItemConsumptions
        )
        selectedItemPositions = mutableListOf()
        binding.markAsPurchase.visibility = View.GONE
    }

    private fun showEditItemDialog(listItemModel: ListItemModel) {
        val dialog = AddItemDialog(
            requireContext(),
            requireActivity(),
            listsViewModel.selectedList.value,
            true,
            listItemModel
        )
        dialog.show(requireActivity().supportFragmentManager, "add_item_dialog")
    }

    private fun showAddItemDialog() {
        val dialog = AddItemDialog(
            requireContext(),
            requireActivity(),
            listsViewModel.selectedList.value
        )
        dialog.show(requireActivity().supportFragmentManager, "add_item_dialog")
    }

    private fun populateGroceryListRecyclerView() {
        binding.inStockRecyclerView.adapter = GroceryListAdapter(
            requireContext(), listOfNonPurchased, listsViewModel
        ).apply {
            onCheckBoxClicked = {
                if (selectedItemPositions.contains(it)) selectedItemPositions.remove(it) else selectedItemPositions.add(
                    it
                )
                binding.shouldVisibleMarkAsPurchaseButton = selectedItemPositions != null && selectedItemPositions.isNotEmpty()
                Toast.makeText(requireContext(), "$it position selected", Toast.LENGTH_SHORT)
                    .show()
            }

            onItemClicked = {
                showEditItemDialog(it)
            }
        }
        binding.inStockRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
    }

    private suspend fun prepareHashmapForPurchasedItems(selectedItemsList: MutableList<ListItemModel>) {
        val firebaseDataManager = FirebaseDataManager.getInstance()
        val updates: MutableMap<String, Any> = HashMap()
        val purchase: MutableMap<String, Any> = HashMap()
        val listId = selectedItemsList[0].listId
        val userId = selectedItemsList[0].userId

        selectedItemsList.forEach {
            updates["${it.id}/purchased"] = true
            updates["${it.id}/purchasedDate"] = System.currentTimeMillis()
            val purchaseId = UUID.randomUUID().toString()
            purchase["${it.listId}/${it.itemId}/${purchaseId}"] = PurchaseHistoryModel(
                purchaseId,
                it.itemId,
                it.listId,
                it.quantity,
                "",
                it.packageSize,
                it.quantity,
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                it.userId,
                it.itemName,
                it.unitId,
                it.categoryId
            )
        }
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun showprogressBar() {
        binding.progressBar.visibility = View.VISIBLE
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        );

    }

}