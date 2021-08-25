package com.tilicho.grocery.mangement.dialog

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.updatePadding
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.CreateListDialogBinding
import com.tilicho.grocery.mangement.sharedPreferences.AppPreff
import com.tilicho.grocery.mangement.utils.ListsModel
import com.tilicho.grocery.mangement.viewModel.ListsViewModel
import java.util.*

class CreateListDialog : DialogFragment() {

    private lateinit var binding: CreateListDialogBinding

    private var rootBottomPadding = 0
    private val KEYBOARD_VISIBLE_THRESHOLD_DP = 100
    private lateinit var listViewModel: ListsViewModel
    var listName:String = ""
    var description:String = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.create_list_dialog,
            null,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner



        rootBottomPadding = binding.root.paddingBottom
        configureInset()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog!!.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog!!.window!!.setGravity(Gravity.BOTTOM)
        dialog!!.window!!.setBackgroundDrawableResource(R.drawable.popup_rounded_top_light_green)
        dialog!!.window!!.attributes.windowAnimations = R.style.ListDialogAnimations
        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        dialog!!.window!!.decorView.systemUiVisibility =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            } else {
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            }

        try {
            val viewModelFactory =
                ViewModelProvider.AndroidViewModelFactory(requireActivity().application)
            listViewModel = requireActivity().run {
                ViewModelProviders.of(this, viewModelFactory).get(ListsViewModel::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            dismiss()
        }

        initListeners()
    }

    private fun initListeners() {
        listViewModel.apply {
            isCreateListSucess = { b: Boolean, s: String ->
                hideProgressBar()
                if (b){
                    dismiss()
                } else{
                    Toast.makeText(requireContext(),s,Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.listNameEdittext.addTextChangedListener {
            binding.enableButton = !(it == null || it.toString().isEmpty())
            listName = it.toString() ?: ""
        }
        binding.discriptionTextFieldEditText.addTextChangedListener {
            description = it.toString() ?: ""
        }
        binding.createListButton.setOnClickListener {
            showprogressBar()
            val listID = UUID.randomUUID().toString()
            listViewModel.createList(
                ListsModel(
                    listID,
                    listName,
                    description,
                    AppPreff(requireContext()).getUserID().toString(),
                    System.currentTimeMillis(),
                    System.currentTimeMillis(),
                    "",
                    false,
                    0,
                    false
                )
            )
        }

        binding.closeDialogButton.setOnClickListener { dismiss() }
    }

    private fun configureInset() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            binding.root.setOnApplyWindowInsetsListener { v, insets ->
                v.updatePadding(bottom = rootBottomPadding + insets.systemWindowInsetBottom)
                insets
            }
        }
    }

    override fun getTheme(): Int = R.style.AppTheme_NoWiredStrapInNavigationBar

    fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        dialog!!.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    fun showprogressBar() {
        binding.progressBar.visibility = View.VISIBLE
        dialog!!.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        );

    }

}