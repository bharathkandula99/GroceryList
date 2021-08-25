package com.tilicho.grocery.mangement.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.activities.HomeActivity
import com.tilicho.grocery.mangement.activities.SplashActivity
import com.tilicho.grocery.mangement.databinding.FragmentSettingsBinding
import com.tilicho.grocery.mangement.dialog.AboutUsDialog
import com.tilicho.grocery.mangement.sharedPreferences.AppPreff
import com.tilicho.grocery.mangement.viewModel.AppViewModelStore
import com.tilicho.grocery.mangement.viewModel.ListsViewModel
import com.tilicho.grocery.mangement.viewModel.SettingsViewModel

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private val appViewModelStore by lazy { AppViewModelStore.getInstance(requireActivity().application) }
    private val settingsViewModel by lazy { appViewModelStore.getAndroidViewModel<SettingsViewModel>() }
    private val listsViewModel by lazy { appViewModelStore.getAndroidViewModel<ListsViewModel>() }

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance();
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

        initListeners()
    }

    private fun initListeners() {
        binding.accountLayout.setOnClickListener {
            binding.root.findNavController()
                .navigate(R.id.action_settingsFragment_to_AccountSettingsFragment)
        }

        binding.teamLayout.setOnClickListener {
            showAboutUs()
        }

        binding.logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun showAboutUs() {
        val dialog = AboutUsDialog()
        dialog.show(requireActivity().supportFragmentManager, "about_us_dialog")
    }

    private fun logout() {
        mAuth!!.signOut()
        AppPreff(requireContext()).clearAppPref()
        listsViewModel.clear(viewLifecycleOwner)
        startActivity(Intent(requireActivity(), SplashActivity::class.java))
    }
}