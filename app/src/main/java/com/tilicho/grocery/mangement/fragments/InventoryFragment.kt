package com.tilicho.grocery.mangement.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.activities.HomeActivity
import com.tilicho.grocery.mangement.adapter.CategoryChooseAdapter
import com.tilicho.grocery.mangement.adapter.FrequentlyUsedItemsAdapter
import com.tilicho.grocery.mangement.adapter.InStockAdapter
import com.tilicho.grocery.mangement.databinding.FragmentInventoryBinding
import com.tilicho.grocery.mangement.utils.ListItemModel
import com.tilicho.grocery.mangement.viewModel.AppViewModelStore
import com.tilicho.grocery.mangement.viewModel.FoodSummeryViewModel
import com.tilicho.grocery.mangement.viewModel.ListsViewModel


class InventoryFragment : Fragment() {

    private lateinit var binding: FragmentInventoryBinding
    private val appViewModelStore by lazy { AppViewModelStore.getInstance(requireActivity().application) }
    private val foodSummeryViewModel by lazy { appViewModelStore.getAndroidViewModel<FoodSummeryViewModel>() }
    private val listsViewModel by lazy { appViewModelStore.getAndroidViewModel<ListsViewModel>() }

    val listOfListItems: MutableList<ListItemModel> = mutableListOf<ListItemModel>()
    var listOfNonPurchasedListItems: MutableList<ListItemModel> = mutableListOf<ListItemModel>()
    private lateinit var instockAdapter: InStockAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button even
                    handleBackPressed()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun handleBackPressed() {
        (activity as HomeActivity?)?.selectFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_inventory, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        //dialog = ProgressDialog(context)
        super.onActivityCreated(savedInstanceState)
        binding.listViewModel = listsViewModel


        initLitiners()
        addObserver()
    }

    private fun addObserver() {
        listsViewModel.listItems.observe(viewLifecycleOwner) {
            try {
                if (it != null && it.isNotEmpty()) {
                    listOfNonPurchasedListItems = mutableListOf()
                    listsViewModel.setInventoryListUIState(false)
                    it.values.forEach { it ->
                        listOfNonPurchasedListItems.addAll(it.values.filter { it.purchased }
                            .toMutableList())
                    }
                    populateInStockRecyclerView()
                } else {
                    listsViewModel.setInventoryListUIState(true)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                listsViewModel.setInventoryListUIState(true)
            }
        }
    }

    private fun populateInStockRecyclerView() {
        instockAdapter = InStockAdapter(
            requireContext(), listOfNonPurchasedListItems, listsViewModel
        ).apply {
            onCardClicked = { it ->
                listsViewModel.setSelectedListItem(it)
                gotoInventoryDetailsScreen(it)
            }
        }
        binding.inStockRecyclerView.adapter = instockAdapter
        binding.inStockRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
    }

    private fun populateFrequentlyUsedRecyclerView() {
        binding.frequentlyUsedRecyclerView.adapter = FrequentlyUsedItemsAdapter(
            requireContext()
        ).apply {
            onCardClicked = {
               // Toast.makeText(requireContext(), "$it position selected", Toast.LENGTH_SHORT).show()
                //gotoInventoryDetailsScreen(it)

            }
        }
        binding.frequentlyUsedRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        binding.frequentlyUsedRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun gotoInventoryDetailsScreen(selectedListItemModel: ListItemModel) {
        val selectedListItemsList: MutableList<ListItemModel> = mutableListOf()
        selectedListItemsList.add(selectedListItemModel)


        val args = bundleOf(
            "selectedListItemModel" to selectedListItemsList,
        )
        binding.root.findNavController().navigate(R.id.action_inventory_to_inventory_details, args)

    }

    private fun initLitiners() {

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("tag", "textChanged $newText")
                performSearchFunctionality(newText)
                return false
            }
        })

        binding.inventoryTitle.setOnClickListener { }

    }

    private fun performSearchFunctionality(newText: String?) {
        instockAdapter.setFilter(newText ?: "")

    }


}