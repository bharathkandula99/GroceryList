package com.tilicho.grocery.mangement.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.FragmentSettingsBinding
import com.tilicho.grocery.mangement.databinding.FragmentUpdatePasswordBinding
import com.tilicho.grocery.mangement.viewModel.AppViewModelStore
import com.tilicho.grocery.mangement.viewModel.SettingsViewModel

class UpdatePassword : Fragment() {
    private lateinit var binding: FragmentUpdatePasswordBinding
    private val appViewModelStore by lazy { AppViewModelStore.getInstance(requireActivity().application) }
    private val settingsViewModel by lazy { appViewModelStore.getAndroidViewModel<SettingsViewModel>() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_password, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        //dialog = ProgressDialog(context)
        super.onActivityCreated(savedInstanceState)
        binding.settingsViewModel = settingsViewModel

        initLitiners()
    }

    private fun initLitiners() {
        binding.backArrow.setOnClickListener {
            binding.root.findNavController().navigateUp()

        }
    }
}