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
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.FragmentUpdatePasswordBinding
import com.tilicho.grocery.mangement.sharedPreferences.AppPreff
import com.tilicho.grocery.mangement.viewModel.AppViewModelStore
import com.tilicho.grocery.mangement.viewModel.SettingsViewModel

class UpdatePassword : Fragment() {
    private lateinit var binding: FragmentUpdatePasswordBinding
    private val appViewModelStore by lazy { AppViewModelStore.getInstance(requireActivity().application) }
    private val settingsViewModel by lazy { appViewModelStore.getAndroidViewModel<SettingsViewModel>() }
    var currentPasswoard = ""
    var newPasswoard = ""
    var confirmPasswoard = ""
    private var mAuth: FirebaseAuth? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_update_password, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        //dialog = ProgressDialog(context)
        super.onActivityCreated(savedInstanceState)
        mAuth = FirebaseAuth.getInstance();
        binding.settingsViewModel = settingsViewModel

        initLitiners()
        validate()
    }

    private fun initLitiners() {
        binding.backArrow.setOnClickListener {
            binding.root.findNavController().navigateUp()

        }
        binding.updatePasswoardButton.setOnClickListener {
            showprogressBar()
            performChangePassword()
        }
        binding.confirmPasswoardEditText.addTextChangedListener {
            confirmPasswoard =
                if (it != null && it.toString() != null && it.toString().isNotEmpty()) {
                    it.toString()
                } else {
                    ""
                }
            validate()
        }
        binding.currentPasswoardEditText.addTextChangedListener {
            currentPasswoard =
                if (it != null && it.toString() != null && it.toString().isNotEmpty()) {
                    it.toString()
                } else {
                    ""
                }
            validate()

        }
        binding.newPasswoardEditText.addTextChangedListener {
            newPasswoard = if (it != null && it.toString() != null && it.toString().isNotEmpty()) {
                it.toString()
            } else {
                ""
            }
            validate()
        }
    }


    private fun performChangePassword() {

        val user = FirebaseAuth.getInstance().currentUser
        val credential = EmailAuthProvider.getCredential(
            AppPreff(requireContext()).getUserEmail().toString(),
            currentPasswoard
        );
        user?.reauthenticate(credential)?.addOnCompleteListener {
            if (it.isSuccessful) {
                user?.updatePassword(confirmPasswoard).addOnCompleteListener {
                    hideProgressBar()
                    Toast.makeText(
                        requireContext(),
                        "Password Successfully Modified",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.root.findNavController().navigateUp()

                }.addOnFailureListener {
                    hideProgressBar()
                    Toast.makeText(
                        requireContext(),
                        "Something went wrong. Please try again later",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                hideProgressBar()
                Toast.makeText(requireContext(), "Invalid current Passwoard", Toast.LENGTH_SHORT)
                    .show()
            }

        }?.addOnFailureListener {
            hideProgressBar()

            Toast.makeText(requireContext(), "Invalid current Passwoard", Toast.LENGTH_SHORT).show()
        }

    }

    fun validate() {

        binding.updatePasswoardButton.isEnabled =
            currentPasswoard != null
                    && currentPasswoard.isNotEmpty()
                    && newPasswoard != null
                    && newPasswoard.isNotEmpty()
                    && confirmPasswoard != null
                    && confirmPasswoard.isNotEmpty()
                    && newPasswoard.lowercase() == confirmPasswoard.lowercase()
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