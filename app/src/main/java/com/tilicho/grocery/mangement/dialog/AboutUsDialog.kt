package com.tilicho.grocery.mangement.dialog

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.AboutUsBinding

class AboutUsDialog : DialogFragment() {

    private lateinit var binding: AboutUsBinding

    private var rootBottomPadding = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.about_us, null, false)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.closeDialogButton.setOnClickListener { dismiss() }
        binding.visitWebsiteButton.setOnClickListener { visitWebsite() }

        rootBottomPadding = binding.root.paddingBottom
        configureInset()

        return binding.root
    }

    override fun onStart() {
        super.onStart()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog!!.window!!.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
        dialog!!.window!!.setGravity(Gravity.BOTTOM)
        dialog!!.window!!.setBackgroundDrawableResource(R.drawable.popup_rounded_top_light_green)
        dialog!!.window!!.attributes.windowAnimations = R.style.ListDialogAnimations
        dialog!!.window!!.decorView.systemUiVisibility = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
        else {
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }
    }

    private fun visitWebsite() {
        val urlString = requireContext().resources.getString(R.string.about_us_url)
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
        startActivity(browserIntent)

        dismiss()
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

}