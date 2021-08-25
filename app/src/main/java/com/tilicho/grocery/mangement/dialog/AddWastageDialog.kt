package com.tilicho.grocery.mangement.dialog

import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.updatePadding
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textview.MaterialTextView
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.AddWastageDialogBinding
import com.tilicho.grocery.mangement.sharedPreferences.AppPreff
import com.tilicho.grocery.mangement.utils.FoodWastageModel
import com.tilicho.grocery.mangement.viewModel.FoodSummeryViewModel
import java.text.SimpleDateFormat
import java.util.*

class AddWastageDialog : DialogFragment() {

    private lateinit var binding: AddWastageDialogBinding
    private lateinit var foodSummeryViewModel: FoodSummeryViewModel

    private var rootBottomPadding = 0
    private val KEYBOARD_VISIBLE_THRESHOLD_DP = 100

    private var chosenDate: Date? = null
    private val dateFormatYearFirst = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    var wastageText: String = ""
    var mealType: String = ""


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.add_wastage_dialog,
            null,
            false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        rootBottomPadding = binding.root.paddingBottom
        configureInset()
        initMealTypeSpinner()


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
            foodSummeryViewModel = requireActivity().run {
                ViewModelProviders.of(this, viewModelFactory).get(FoodSummeryViewModel::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            dismiss()
        }
        addObservers()
        initListeners()
        initLastPaymentDatePicker()
    }

    private fun addObservers() {
        foodSummeryViewModel.apply {
            isCreateWastageListSuccess = { b: Boolean, s: String ->
                hideProgressBar()
                if (b) {
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initListeners() {

        binding.closeDialogButton.setOnClickListener {
            dismiss()
        }
        binding.addWastageEdittext.addTextChangedListener {
            if (it != null && it.toString().isNotEmpty()) {
                wastageText = it.toString()

            }
            validateButton()
        }
        binding.saveButton.setOnClickListener {
            showprogressBar()
            foodSummeryViewModel.addWastageEntry(
                FoodWastageModel(
                    UUID.randomUUID().toString(),
                    AppPreff(requireContext()).getUserID().toString(),
                    (wastageText.toDouble()) as Double,
                    chosenDate?.time ?: System.currentTimeMillis(),
                    mealType
                )
            )

        }
    }

    private fun validateButton() {
        binding.enableButton =
            wastageText != null && wastageText.isNotEmpty() && chosenDate != null && mealType != null && mealType.isNotEmpty()
    }

    private fun configureInset() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            binding.root.setOnApplyWindowInsetsListener { v, insets ->
                v.updatePadding(bottom = rootBottomPadding + insets.systemWindowInsetBottom)
                insets
            }
        }
    }

    private fun initMealTypeSpinner() {
        val packageUnitsAdapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.type_of_meal,
            R.layout.dropdown_menu
        ).also { arrayAdapter ->
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        binding.unitsSpinner.adapter = packageUnitsAdapter
        // binding.unitsSpinner.setSelection(0) // 0 default

        binding.unitsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                /*Toast.makeText(
                    requireContext(),
                    (view as MaterialTextView).text,
                    Toast.LENGTH_SHORT
                ).show()*/
                mealType = (view as MaterialTextView).text as String
                validateButton()
            }
        }
    }

    private fun initLastPaymentDatePicker() {
        binding.wastageOnEditText.setOnClickListener {
            val datePicker = CalendarDatePickerDialog(requireContext(), chosenDate).apply {
                dateChangeHandler = { y, m, d -> selectLastPaymentDate(y, m, d) }
            }
            datePicker.show()
        }
    }

    private fun selectLastPaymentDate(y: Int, m: Int, d: Int) {
        val c = Calendar.getInstance()
        c.set(y, m, d)
        chosenDate = c.time
        binding.wastageOnEditText.setText(dateFormatYearFirst.format(chosenDate!!))
        validateButton()
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