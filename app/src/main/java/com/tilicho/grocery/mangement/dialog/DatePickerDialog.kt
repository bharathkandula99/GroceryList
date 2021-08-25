package com.tilicho.grocery.mangement.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.CalanderDatePickerBinding
import java.text.SimpleDateFormat
import java.util.*

class CalendarDatePickerDialog(context: Context, private val defaultDate: Date? = null): Dialog(context) {

    private lateinit var binding: CalanderDatePickerBinding

    var dateChangeHandler: ((year: Int, monthOfYear: Int, dayOfMonth: Int) -> Unit)? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.calander_date_picker, null, false)

        binding.chooseDateButton.setOnClickListener {
            val y = binding.calendarDatePicker.year
            val m = binding.calendarDatePicker.month
            val d = binding.calendarDatePicker.dayOfMonth
            dateChangeHandler?.invoke(y, m, d)
            dismiss()
        }

        binding.cancelButton.setOnClickListener { dismiss() }

        binding.calendarDatePicker.maxDate = Date().time

        updateViewToDefaultDate()

        setCanceledOnTouchOutside(false)

        setContentView(binding.root)
    }

    private fun updateViewToDefaultDate() {
        if (defaultDate != null) {
            val c = Calendar.getInstance()
            c.time = defaultDate
            binding.calendarDatePicker.updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(
                Calendar.DAY_OF_MONTH))
        }
    }

    override fun onStart() {
        super.onStart()
        window!!.attributes.windowAnimations = R.style.PopDialogAnimations
    }

    /*fun selectWastageOnDate(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val c = Calendar.getInstance()
        c.set(year, monthOfYear, dayOfMonth)
        chosenDate = c.time

        binding.onW.setText(dateFormatYearFirst.format(chosenDate!!))

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            binding.reminderViewsContainer.foreground = null
        }
        binding.reminderEnabledSwitch.isClickable = true
        switchReminderRadioButtonsEnabled(binding.reminderEnabledSwitch.isChecked)
    }*/
}