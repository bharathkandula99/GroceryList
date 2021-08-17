package com.tilicho.grocery.mangement.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.adapter.GroceryListAdapter
import com.tilicho.grocery.mangement.databinding.FragmentGroceryListBinding
import com.tilicho.grocery.mangement.dialog.AddItemDialog
import com.tilicho.grocery.mangement.viewModel.AppViewModelStore
import com.tilicho.grocery.mangement.viewModel.ListsViewModel

class GroceryListFragment : Fragment() {

    private lateinit var binding: FragmentGroceryListBinding
    private val appViewModelStore by lazy { AppViewModelStore.getInstance(requireActivity().application) }
    private val listsViewModel by lazy { appViewModelStore.getAndroidViewModel<ListsViewModel>() }


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
        initLitiners()
        addObservers()
        populateGroceryListRecyclerView()

    }

    private fun addObservers() {
        listsViewModel.shouldShowGroceryListEmptyUI.observe(viewLifecycleOwner) {

            if (it) {
                populateGroceryListRecyclerView()
            }
        }

    }

    private fun initLitiners() {
        binding.backArrow.setOnClickListener {
            binding.root.findNavController().navigateUp()
        }

        binding.createGroceryListButton.setOnClickListener {
            showAddItemDialog()
        }
        binding.floatingActionButton.setOnClickListener { showAddItemDialog() }
        binding.groceryListTitle.setOnClickListener { listsViewModel.setGroceryListUIState(!listsViewModel.shouldShowGroceryListEmptyUI.value!!) }
    }

    private fun showAddItemDialog() {
        val dialog = AddItemDialog()
        dialog.show(requireActivity().supportFragmentManager, "add_item_dialog")
    }

    private fun populateGroceryListRecyclerView() {
        binding.inStockRecyclerView.adapter = GroceryListAdapter(
            requireContext()
        ).apply {
            onCardClicked = {
                Toast.makeText(requireContext(), "$it position selected", Toast.LENGTH_SHORT).show()
            }
        }
        binding.inStockRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())

    }


}