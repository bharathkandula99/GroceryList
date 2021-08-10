package com.tilicho.grocery.mangement.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.FragmentLoginBinding
import com.tilicho.grocery.mangement.databinding.FragmentSettingsBinding
import com.tilicho.grocery.mangement.dialog.AboutUsDialog
import com.tilicho.grocery.mangement.viewModel.AppViewModelStore
import com.tilicho.grocery.mangement.viewModel.AuthViewModel
import com.tilicho.grocery.mangement.viewModel.SettingsViewModel

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val appViewModelStore by lazy { AppViewModelStore.getInstance(requireActivity().application) }
    private val settingsViewModel by lazy { appViewModelStore.getAndroidViewModel<SettingsViewModel>() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false)
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

        binding.accountLayout.setOnClickListener {
            binding.root.findNavController()
                .navigate(R.id.action_settingsFragment_to_AccountSettingsFragment)
        }

        binding.teamLayout.setOnClickListener {showAboutUs()  }
    }
    private fun showAboutUs() {
        val dialog = AboutUsDialog()
        dialog.show(requireActivity().supportFragmentManager, "about_us_dialog")
    }
}