package com.tilicho.grocery.mangement.fragments

import android.R.attr.password
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.activities.HomeActivity
import com.tilicho.grocery.mangement.api.FirebaseAuthManager
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

    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth = FirebaseAuth.getInstance();
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
        authViewModel.setUIState(false)

        initListeners()
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
                firstNameOk = it.length > 2
                toggleActionButtons()
            }
        }

        binding.lastNameEditText.addTextChangedListener {
            if (it != null) {
                lastNameOk = it.length > 2
                toggleActionButtons()
            }
        }

        binding.emailEditText.addTextChangedListener {
            if (it != null) {
                emailOk = it.length > 2
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
            triggerSignUp(binding.emailEditText.text.toString(), binding.passwordEditText.text.toString())
        }

        binding.loginButton.setOnClickListener {
            triggerSignIn(binding.emailEditText.text.toString(), binding.passwordEditText.text.toString())
        }

        binding.forgotPasswordTextView.setOnClickListener {
            triggerForgotPassword("dheeraj@tilicho.in")
        }
    }

    private fun triggerSignUp(email: String, password: String) {
        val authResultLiveData = FirebaseAuthManager.getInstance().performSignUp(email, password)

        authResultLiveData.observe(this.viewLifecycleOwner, {
            if (it.isSuccessful) {
                Toast.makeText(context, "Signup success.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireActivity(), HomeActivity::class.java))

                // Using this User ID start with RealTime DB integration
                // it.result.user.uid
                //TODO Register first name and last name
            } else {
                Toast.makeText(context, "Signup failed.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun triggerSignIn(email: String, password: String) {
        val authResultLiveData = FirebaseAuthManager.getInstance().performSignIn(email, password)

        authResultLiveData.observe(this.viewLifecycleOwner, {
            if (it.isSuccessful) {
                Toast.makeText(context, "Login success.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireActivity(), HomeActivity::class.java))
            } else {
                Toast.makeText(context, "Login failed.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun triggerForgotPassword(email: String) {
        val authResultLiveData = FirebaseAuthManager.getInstance().performForgotPassword(email)

        authResultLiveData.observe(this.viewLifecycleOwner, {
            if(it.isSuccessful){
                Toast.makeText(context, "Reset password link sent success.", Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireActivity(), HomeActivity::class.java))
            } else {
                Toast.makeText(context, "Reset password link sent failed.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}