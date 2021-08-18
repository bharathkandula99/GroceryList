package com.tilicho.grocery.mangement.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.FragmentResetPasswordBinding
import com.tilicho.grocery.mangement.viewModel.AppViewModelStore
import com.tilicho.grocery.mangement.viewModel.AuthViewModel

class ResetPasswordFragment : Fragment() {

    private lateinit var binding: FragmentResetPasswordBinding
    private val appViewModelStore by lazy { AppViewModelStore.getInstance(requireActivity().application) }
    private val authViewModel by lazy { appViewModelStore.getAndroidViewModel<AuthViewModel>() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_reset_password, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        //dialog = ProgressDialog(context)
        super.onActivityCreated(savedInstanceState)
        binding.authViewModel = authViewModel

        initListeners()
        addObservers()
    }

    private fun addObservers() {
        authViewModel._isRestPasswordnSucess.observe(this.viewLifecycleOwner, {
            if (it) {
                hideProgressBar()
                Toast.makeText(context, "Send sucess.", Toast.LENGTH_SHORT)
                    .show()
                binding.root.findNavController().navigateUp()
                authViewModel._isRestPasswordnSucess.value = false
            }
        })

        authViewModel._isRestPasswordFailed.observe(this.viewLifecycleOwner, {
            if (it) {
                hideProgressBar()
                Toast.makeText(
                    context,
                    "Send link failed." + authViewModel.resetPasswoardAuthResult.value?.errorMessage,
                    Toast.LENGTH_SHORT
                )
                    .show()
                authViewModel._isRestPasswordFailed.value = false
            }

        })
    }

    private fun initListeners() {
        binding.emailEditText.addTextChangedListener {
            binding.isEmailOk = !(it == null || it.toString()
                .isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(it.toString()).matches())
        }
        binding.sendLink.setOnClickListener {
            showprogressBar()
            authViewModel.performResetPassword(binding.emailEditText.text.toString() ?: "")
        }

        binding.backArrow.setOnClickListener {
            binding.root.findNavController().navigateUp()
        }
    }

    fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    fun showprogressBar() {
        binding.progressBar.visibility = View.VISIBLE
        activity?.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        );

    }
}