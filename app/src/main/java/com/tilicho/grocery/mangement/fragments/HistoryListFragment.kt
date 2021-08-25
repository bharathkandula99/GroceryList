package com.tilicho.grocery.mangement.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.adapter.MyListsAdapter
import com.tilicho.grocery.mangement.databinding.FragmentMyListsBinding
import com.tilicho.grocery.mangement.utils.ListItemModel
import com.tilicho.grocery.mangement.utils.ListsModel
import com.tilicho.grocery.mangement.viewModel.AppViewModelStore
import com.tilicho.grocery.mangement.viewModel.ListsViewModel

class HistoryListFragment : Fragment() {

    private lateinit var binding: FragmentMyListsBinding
    private val appViewModelStore by lazy { AppViewModelStore.getInstance(requireActivity().application) }
    private val listsViewModel by lazy { appViewModelStore.getAndroidViewModel<ListsViewModel>() }
    private var historyHashMap: HashMap<String, ListsModel> = HashMap<String, ListsModel>()

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

        // populateListsRecyclerView(lis)

        initListeners()
        addObservers()
    }

    private fun populateListsRecyclerView(lists: List<ListsModel>) {
        binding.myListsRecyclerView.adapter = MyListsAdapter(
            requireContext(),
            lists,
            listsViewModel
        ).apply {
            onCardClicked = {
               Toast.makeText(requireContext(),"history item clicked",Toast.LENGTH_SHORT).show()
            }
        }
        binding.myListsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        binding.myListsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }

    private fun addObservers() {
        listsViewModel.lists.observe(viewLifecycleOwner) { it ->
            if (it != null && it.isNotEmpty()) {
                val list = ArrayList(it.values)
                prepareDataAndPopulateRecyclerView(false, it)
            }
        }
        listsViewModel.listItems.observe(viewLifecycleOwner) {
            prepareDataAndPopulateRecyclerView(true)
        }
    }

    private fun initListeners() {
        //("Not yet implemented")
    }
    fun prepareDataAndPopulateRecyclerView(
        isListItemsObserved: Boolean,
        lists: HashMap<String, ListsModel>? = null

    ) {

        if (listsViewModel.lists.value != null && (isListItemsObserved || listsViewModel.listItems.value != null)) {
            historyHashMap = HashMap<String, ListsModel>()

            listsViewModel.lists.value?.values?.forEach { it ->
                if (listsViewModel.listItems.value != null) {
                    val listItems =
                        listsViewModel.listItems.value!!.get(it?.id)?.values
                    if (listItems != null && listItems.isNotEmpty()) {
                        val listOfPurchasedItems =
                            listItems.filter { it.purchased } as MutableList<ListItemModel>
                        if ((listOfPurchasedItems != null && listOfPurchasedItems.isNotEmpty() && listOfPurchasedItems.size == listItems.size)) {
                            historyHashMap?.put(it.id, it)
                        }
                    }
                }
            }
        }

        val list = ArrayList(historyHashMap.values)
        populateListsRecyclerView(list)
    }


}