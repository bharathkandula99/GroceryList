package com.tilicho.grocery.mangement.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.adapter.MyListsAdapter
import com.tilicho.grocery.mangement.databinding.FragmentListsBinding
import com.tilicho.grocery.mangement.databinding.FragmentMyListsBinding
import com.tilicho.grocery.mangement.viewModel.AppViewModelStore
import com.tilicho.grocery.mangement.viewModel.ListsViewModel

class MyListsFragment : Fragment() {

    private lateinit var binding: FragmentMyListsBinding
    private val appViewModelStore by lazy { AppViewModelStore.getInstance(requireActivity().application) }
    private val listsViewModel by lazy { appViewModelStore.getAndroidViewModel<ListsViewModel>() }

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

        populateListsRecyclerView()

        initListeners()
        addObservers()
    }

    private fun populateListsRecyclerView() {
        binding.myListsRecyclerView.adapter = MyListsAdapter(
            requireContext()
        ).apply {
            onCardClicked = {
                navigateToGroceryListFragment()
            }
        }
        binding.myListsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
        binding.myListsRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

    }

    private fun navigateToGroceryListFragment() {
        binding.root.findNavController()
            .navigate(R.id.action_mylists_to_grocerylists)
    }

    private fun addObservers() {
        //("Not yet implemented")
    }

    private fun initListeners() {
        //("Not yet implemented")
    }

}