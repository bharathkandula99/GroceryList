package com.tilicho.grocery.mangement.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.activities.HomeActivity
import com.tilicho.grocery.mangement.databinding.FragmentLoginBinding
import com.tilicho.grocery.mangement.viewModel.AppViewModelStore
import com.tilicho.grocery.mangement.viewModel.AuthViewModel


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val appViewModelStore by lazy { AppViewModelStore.getInstance(requireActivity().application) }
    private val authViewModel by lazy { appViewModelStore.getAndroidViewModel<AuthViewModel>() }

    private var firstNameOk = false
    private var lastNameOk = false
    private var emailOk = false
    private var passwordOk = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        //dialog = ProgressDialog(context)
        super.onActivityCreated(savedInstanceState)
        binding.viewModel = authViewModel
        authViewModel.setUIState(true)

        initListeners()
        addObservers()
    }

    private fun addObservers() {
        authViewModel._isLoginSucess.observe(this.viewLifecycleOwner, {
            if (it) {
                hideProgressBar()
                Toast.makeText(context, "Login sucess.", Toast.LENGTH_SHORT)
                    .show()
                navigateToHomeActivity()
                authViewModel._isLoginSucess.value = false
            }
        })

        authViewModel._isLoginFailed.observe(this.viewLifecycleOwner, {
            if (it) {
                hideProgressBar()
                Toast.makeText(
                    context,
                    "Login failed." + authViewModel.loginAuthResult.value?.errorMessage,
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })

        authViewModel._isSignUpSucess.observe(this.viewLifecycleOwner, {
            if (it) {
                hideProgressBar()
                Toast.makeText(context, "Sign up sucess.", Toast.LENGTH_SHORT)
                    .show()
                navigateToHomeActivity()
                authViewModel._isSignUpSucess.value = false
            }
        })

        authViewModel._isSignUpFailed.observe(this.viewLifecycleOwner, {
            if (it) {
                hideProgressBar()
                Toast.makeText(
                    context,
                    "Signup failed." + authViewModel.signUpAuthResult.value?.errorMessage,
                    Toast.LENGTH_SHORT
                )
                    .show()
            }

        })
    }

    private fun toggleActionButtons() {
        if (authViewModel.loginUIShown.value!!) {
            binding.enableLoginButton = emailOk && passwordOk
        } else if (!authViewModel.loginUIShown.value!!) {
            binding.enableSignupButton = firstNameOk && lastNameOk && emailOk && passwordOk
        }

    }

    private fun initListeners() {
        binding.firstNameEditText.addTextChangedListener {
            if (it != null) {
                firstNameOk = it.length > 4
                authViewModel.firstName.value = it.toString()
                toggleActionButtons()
            }
        }

        binding.lastNameEditText.addTextChangedListener {
            if (it != null) {
                lastNameOk = it.length > 2
                authViewModel.lastName.value = it.toString()
                toggleActionButtons()
            }
        }

        binding.emailEditText.addTextChangedListener {
            if (it != null && it.toString() != null && it.toString().isNotEmpty()) {
                emailOk =
                    it.length > 2 && android.util.Patterns.EMAIL_ADDRESS.matcher(it.toString())
                        .matches()
                toggleActionButtons()
            }
        }

        binding.passwordEditText.addTextChangedListener {
            if (it != null) {
                passwordOk = it.length > 7
                toggleActionButtons()
            }
        }

        binding.alreadyTV.setOnClickListener {
            authViewModel.setUIState(true)
        }

        binding.dontHaveTV.setOnClickListener {
            authViewModel.setUIState(false)
        }

        binding.createAccountButton.setOnClickListener {
            showProgressBar()
            authViewModel.performSignUp(
                binding.emailEditText.text.toString(),
                binding.passwordEditText.text.toString()
            )
        }

        binding.loginButton.setOnClickListener {
            showProgressBar()
            authViewModel.performLogin(
                binding.emailEditText.text.toString(),
                binding.passwordEditText.text.toString()
            )
        }

        binding.forgotPasswordTextView.setOnClickListener {
            Toast.makeText(context, "Work in progress", Toast.LENGTH_SHORT).show()
            binding.root.findNavController()
                .navigate(R.id.login_to_reset_password)
            //  triggerForgotPassword("dheeraj@tilicho.in")
        }
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }

    fun navigateToHomeActivity() {
        authViewModel._isSignUpSucess.removeObservers(viewLifecycleOwner)
        authViewModel._isLoginSucess.removeObservers(viewLifecycleOwner)
        startActivity(Intent(requireActivity(), HomeActivity::class.java))
        activity?.finish()
    }
}