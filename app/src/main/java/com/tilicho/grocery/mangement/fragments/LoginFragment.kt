package com.tilicho.grocery.mangement.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.activities.HomeActivity
import com.tilicho.grocery.mangement.databinding.FragmentLoginBinding
import com.tilicho.grocery.mangement.viewModel.AppViewModelStore
import com.tilicho.grocery.mangement.viewModel.AuthViewModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val appViewModelStore by lazy { AppViewModelStore.getInstance(requireActivity().application) }

    private val authViewModel by lazy { appViewModelStore.getAndroidViewModel<AuthViewModel>() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        //dialog = ProgressDialog(context)
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = authViewModel

        initLitiners()
    }

    private fun initLitiners() {
        binding.alreadyTV.setOnClickListener {
            authViewModel.setUIState(true)

            //binding.showLoginUI = true
        }

        binding.dontHaveTV.setOnClickListener {
            authViewModel.setUIState(false)

           // binding.showLoginUI = false
        }
        binding.createAccountButton.setOnClickListener {
            Toast.makeText(requireContext(),"create",Toast.LENGTH_SHORT)
                .show()
        }
        binding.loginButton.setOnClickListener {
            startActivity(Intent(requireActivity(), HomeActivity::class.java))
        }
    }

}