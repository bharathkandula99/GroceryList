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
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.CreateListDialogBinding

class CreateListDialog : DialogFragment() {

    private lateinit var binding: CreateListDialogBinding

    private var rootBottomPadding = 0
    private val KEYBOARD_VISIBLE_THRESHOLD_DP = 100

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.create_list_dialog, null, false)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.closeDialogButton.setOnClickListener { dismiss() }

        rootBottomPadding = binding.root.paddingBottom
        configureInset()

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
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager


        if (imm.isAcceptingText) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }
    }


    fun Activity.isKeyboardOpen(): Boolean {
        fun convertDpToPx(value: Int): Int =
            (value * Resources.getSystem().displayMetrics.density).toInt()

        val rootView = findViewById<View>(android.R.id.content)
        val visibleThreshold = Rect()
        rootView.getWindowVisibleDisplayFrame(visibleThreshold)
        val heightDiff = rootView.height - visibleThreshold.height()

        val accessibleValue = convertDpToPx(KEYBOARD_VISIBLE_THRESHOLD_DP)

        return heightDiff > accessibleValue
    }

    override fun getTheme(): Int = R.style.AppTheme_NoWiredStrapInNavigationBar

}