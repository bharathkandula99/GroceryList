package com.tilicho.grocery.mangement.dialog

import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.AddItemDialogBinding

class AddItemDialog: DialogFragment() {

    private lateinit var binding: AddItemDialogBinding

    private var rootBottomPadding = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.add_item_dialog,
            null,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner


        rootBottomPadding = binding.root.paddingBottom
        configureInset()
        initPackageUnitAdapter()



        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog!!.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog!!.window!!.setGravity(Gravity.BOTTOM)
        dialog!!.window!!.setBackgroundDrawableResource(R.drawable.popup_rounded_top_light_green)
        dialog!!.window!!.attributes.windowAnimations = R.style.ListDialogAnimations
        dialog!!.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)


        dialog!!.window!!.decorView.systemUiVisibility = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
        else {
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
        initLitiners()

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)

    }

    override fun getTheme(): Int = R.style.AppTheme_NoWiredStrapInNavigationBar

    private fun initLitiners() {
        binding.closeDialogButton.setOnClickListener { dismiss() }


        //For spinner
        binding.autoCompleteTextView.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

            }
        }


        binding.cateogryButton.setOnClickListener {
            val dialog = CategoryDialog().apply {
                categorySelected = {
                    binding.cateogryButton.text = it
                }
            }
            activity?.let { it1 -> dialog.show(it1.supportFragmentManager, "currency_chooser_dialog") }
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

    private fun initPackageUnitAdapter() {

        val packageUnitsAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.programming_languages,
            R.layout.dropdown_menu).also {
                arrayAdapter ->
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.autoCompleteTextView.setAdapter(packageUnitsAdapter)
    }



    /*override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val inflater: MenuInflater = inflater
        inflater.inflate(R.menu.overflow_menu, menu)
        //return true
    }*/
}