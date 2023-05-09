package com.tilicho.grocery.mangement.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.adapter.MyListsAdapter
import com.tilicho.grocery.mangement.databinding.FragmentMyListsBinding
import com.tilicho.grocery.mangement.utils.ListItemModel
import com.tilicho.grocery.mangement.utils.ListsModel
import com.tilicho.grocery.mangement.viewModel.AppViewModelStore
import com.tilicho.grocery.mangement.viewModel.ListsViewModel

class MyListsFragment : Fragment() {

    private lateinit var binding: FragmentMyListsBinding
    private val appViewModelStore by lazy { AppViewModelStore.getInstance(requireActivity().application) }
    private val listsViewModel by lazy { appViewModelStore.getAndroidViewModel<ListsViewModel>() }
    var currentHashMap: HashMap<String, ListsModel> = HashMap<String, ListsModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_my_lists, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        //dialog = ProgressDialog(context)
        super.onActivityCreated(savedInstanceState)
        binding.listViewModel = listsViewModel

        // populateListsRecyclerView()

        initListeners()
        addObservers()
    }

    private fun populateListsRecyclerView(lists: List<ListsModel>) {
        binding.myListsRecyclerView.adapter = MyListsAdapter(
            requireContext(), lists,listsViewModel
        ).apply {
            onCardClicked = {
                navigateToGroceryListFragment(it)
            }
        }
        binding.myListsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        binding.myListsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }

    private fun navigateToGroceryListFragment(selectedList: ListsModel) {
        listsViewModel.setSelectedList(selectedList)
        binding.root.findNavController()
            .navigate(R.id.action_mylists_to_grocerylists)
    }

    private fun addObservers() {
        listsViewModel.lists.observe(viewLifecycleOwner) { it ->
            //val listId  = it.keys.firstOrNull()
            // val items = listsViewModel.items.value?.get(listId)?.filterValues { it.isPurchased }?.keys
            //val listID  = it.values.forEach {it.id }
            if (it != null && it.isNotEmpty()) {

                binding.shouldShowEmptyUI = false
                prepareDataAndPopulateRecyclerView(false, it)
            } else {
                binding.shouldShowEmptyUI = true
            }
        }

        listsViewModel.listItems.observe(viewLifecycleOwner) {
            prepareDataAndPopulateRecyclerView(true)
        }
    }

    private fun prepareDataAndPopulateRecyclerView(
        isListItemsObserved: Boolean,
        lists: HashMap<String, ListsModel>? = null

    ) {

        if (listsViewModel.lists.value != null && (isListItemsObserved || listsViewModel.listItems.value != null)) {

            currentHashMap = HashMap<String, ListsModel>()
            listsViewModel.lists.value?.values?.forEach { it ->
                if (listsViewModel.listItems.value != null) {
                    val listItems =
                        listsViewModel.listItems.value!!.get(it?.id)?.values
                    if (listItems != null && listItems.isNotEmpty()) {
                        val listOfPurchasedItems =
                            listItems.filter { it.purchased } as MutableList<ListItemModel>
                        if (!(listOfPurchasedItems != null && listOfPurchasedItems.isNotEmpty() && listOfPurchasedItems.size == listItems.size)) {
                            currentHashMap?.put(it.id, it)
                        }
                    } else {
                        currentHashMap?.put(it.id, it)
                    }
                } else {
                    currentHashMap?.put(it.id, it)
                }
            }
        }

        val list = ArrayList(currentHashMap.values)
        if (list != null && list.isNotEmpty()) {
            populateListsRecyclerView(list)
            binding.shouldShowEmptyUI = false
        } else {
            binding.shouldShowEmptyUI = true
        }
    }

    private fun initListeners() {
        //("Not yet implemented")
    }

}