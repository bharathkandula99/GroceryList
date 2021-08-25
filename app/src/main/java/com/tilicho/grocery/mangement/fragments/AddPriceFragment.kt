package com.tilicho.grocery.mangement.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.adapter.AddPriceAdapter
import com.tilicho.grocery.mangement.databinding.FragmentAddPriceBinding
import com.tilicho.grocery.mangement.utils.ItemConsumptions
import com.tilicho.grocery.mangement.utils.ListItemModel
import com.tilicho.grocery.mangement.utils.PurchaseHistoryModel
import com.tilicho.grocery.mangement.viewModel.AppViewModelStore
import com.tilicho.grocery.mangement.viewModel.ListsViewModel

class AddPriceFragment : Fragment() {

    private lateinit var binding: FragmentAddPriceBinding
    private val appViewModelStore by lazy { AppViewModelStore.getInstance(requireActivity().application) }
    private val listsViewModel by lazy { appViewModelStore.getAndroidViewModel<ListsViewModel>() }
    val selectedListItemsList: MutableList<ListItemModel> = mutableListOf()
    private val createNewPurchaseHistory: MutableList<PurchaseHistoryModel> = mutableListOf()
    val createNewItemConsumptions: MutableList<ItemConsumptions> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_add_price, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        //dialog = ProgressDialog(context)
        super.onActivityCreated(savedInstanceState)
        if (arguments?.get("selectedListItemsList") != null) {
            selectedListItemsList.addAll(arguments?.get("selectedListItemsList") as MutableList<ListItemModel>)
        }
        if (arguments?.get("createNewPurchaseHistory") != null) {
            createNewPurchaseHistory.addAll(arguments?.get("createNewPurchaseHistory") as MutableList<PurchaseHistoryModel>)
        }
        if (arguments?.get("createNewItemConsumptions") != null) {
            createNewItemConsumptions.addAll(arguments?.get("createNewItemConsumptions") as MutableList<ItemConsumptions>)
        }
        binding.listViewModel = listsViewModel
        initListeners()
        addObservers()
        populateAddPriceRecyclerView()

    }

    private fun populateAddPriceRecyclerView() {
        binding.inStockRecyclerView.adapter = AddPriceAdapter(
            requireContext(), selectedListItemsList, createNewPurchaseHistory, listsViewModel
        ).apply {

            onPriceEdited = { id, priceString ->
                if (createNewPurchaseHistory.filter { it.id == id }[0] != null) {
                    createNewPurchaseHistory.filter { it.id == id }[0].price = priceString ?: ""
                }
            }
        }
        binding.inStockRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
    }

    private fun addObservers() {

        listsViewModel.apply {
            isPurchaseSuccess = { b: Boolean, s: String ->
                hideProgressBar()
                if (!b) {
                    Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
                } else {
                    binding.root.findNavController().navigateUp()
                }
            }
        }
    }

    private fun initListeners() {

        binding.doneButton.setOnClickListener {
            showprogressBar()
            listsViewModel.markAsAPurchase(
                selectedListItemsList,
                createNewPurchaseHistory,
                createNewItemConsumptions
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