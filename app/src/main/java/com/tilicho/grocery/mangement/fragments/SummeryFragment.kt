package com.tilicho.grocery.mangement.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.FragmentInventoryBinding
import com.tilicho.grocery.mangement.databinding.FragmentSummeryBinding
import com.tilicho.grocery.mangement.viewModel.AppViewModelStore
import com.tilicho.grocery.mangement.viewModel.FoodSummeryViewModel
import com.tilicho.grocery.mangement.viewModel.InventoryViewModel

class SummeryFragment : Fragment() {

    private lateinit var binding: FragmentSummeryBinding
    private val appViewModelStore by lazy { AppViewModelStore.getInstance(requireActivity().application) }
    private val foodSummeryViewModel by lazy { appViewModelStore.getAndroidViewModel<FoodSummeryViewModel>() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_summery, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        //dialog = ProgressDialog(context)
        super.onActivityCreated(savedInstanceState)
        binding.foodSummeryViewModel = foodSummeryViewModel

        initLitiners()
    }

    private fun initLitiners() {

    }


}