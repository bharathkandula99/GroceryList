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
import com.tilicho.grocery.mangement.adapter.FrequentlyUsedItemsAdapter
import com.tilicho.grocery.mangement.adapter.InStockAdapter
import com.tilicho.grocery.mangement.databinding.FragmentInventoryBinding
import com.tilicho.grocery.mangement.viewModel.AppViewModelStore
import com.tilicho.grocery.mangement.viewModel.InventoryViewModel

class InventoryFragment : Fragment() {

    private lateinit var binding: FragmentInventoryBinding
    private val appViewModelStore by lazy { AppViewModelStore.getInstance(requireActivity().application) }
    private val inventoryViewModel by lazy { appViewModelStore.getAndroidViewModel<InventoryViewModel>() }


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
        binding.inventoryViewModel = inventoryViewModel
        populateFrequentlyUsedRecyclerView()
        populateInStockRecyclerView()
        initLitiners()
    }

    private fun populateInStockRecyclerView() {
        binding.inStockRecyclerView.adapter = InStockAdapter(
            requireContext()
        ).apply {
            onCardClicked = {
                Toast.makeText(requireContext(), "$it position selected", Toast.LENGTH_SHORT).show()
                gotoInventoryDetailsScreen()

            }
        }
        binding.inStockRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())

    }

    private fun populateFrequentlyUsedRecyclerView() {
        binding.frequentlyUsedRecyclerView.adapter = FrequentlyUsedItemsAdapter(
            requireContext()
        ).apply {
            onCardClicked = {
                Toast.makeText(requireContext(), "$it position selected", Toast.LENGTH_SHORT).show()
                gotoInventoryDetailsScreen()

            }
        }
        binding.frequentlyUsedRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        binding.frequentlyUsedRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun gotoInventoryDetailsScreen() {

        binding.root.findNavController()
            .navigate(R.id.action_inventory_to_inventory_details)

    }

    private fun initLitiners() {

        binding.inventoryTitle.setOnClickListener { inventoryViewModel.setUIState(!inventoryViewModel.shouldShowEmptyUI.value!!) }

    }




}