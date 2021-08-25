package com.tilicho.grocery.mangement.dialog

import android.content.Context
import android.content.DialogInterface
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
import com.tilicho.grocery.mangement.databinding.CreateCateogryDialogBinding
import com.tilicho.grocery.mangement.sharedPreferences.AppPreff
import com.tilicho.grocery.mangement.utils.CategoryModel
import com.tilicho.grocery.mangement.viewModel.ListsViewModel
import java.util.*

class CreateCateogryDialog : DialogFragment() {

    private lateinit var binding: CreateCateogryDialogBinding
    private lateinit var listViewModel: ListsViewModel
    private var categoryName: String = ""


    private var rootBottomPadding = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.create_cateogry_dialog,
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

        initDialogWindow()
        initViewModel()
        initListiners()


    }

    fun initDialogWindow() {
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
    }

    private fun initListiners() {
        binding.closeDialogButton.setOnClickListener { dismiss() }
        listViewModel.apply {
            isCreateCategorySucess = { b: Boolean, s: String ->
                hideProgressBar()
                if (b) {
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.categoryEditText.addTextChangedListener {
            binding.enableButton = !(it == null || it.toString().isEmpty())
            categoryName = it.toString() ?: ""
        }

        binding.createCategoryDialogButton.setOnClickListener {
            showprogressBar()
            val newCategoryId = UUID.randomUUID().toString()
            listViewModel.createCategory(
                CategoryModel(
                    newCategoryId,
                    AppPreff(requireContext()).getUserID().toString(),
                    categoryName,
                    "",
                    System.currentTimeMillis(),
                    System.currentTimeMillis()
                )
            )
        }
    }

    private fun configureInset() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            binding.root.setOnApplyWindowInsetsListener { v, insets ->
                v.updatePadding(bottom = rootBottomPadding + insets.systemWindowInsetBottom)
                insets
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)

    }

    fun initViewModel() {
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
    }

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


    override fun getTheme(): Int = R.style.AppTheme_NoWiredStrapInNavigationBar

}