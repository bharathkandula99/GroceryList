package com.tilicho.grocery.mangement.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.widget.SearchView
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.adapter.CategoryChooseAdapter
import com.tilicho.grocery.mangement.databinding.CategoryDialogLayoutBinding

class CategoryDialog : DialogFragment() {
    private lateinit var binding: CategoryDialogLayoutBinding


    var categorySelected: ((String) -> Unit)? = null
    private lateinit var categoryAdapter: CategoryChooseAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.category_dialog_layout, null, false)

        binding.lifecycleOwner = viewLifecycleOwner

        categoryAdapter = CategoryChooseAdapter(requireContext())
        binding.currencyListView.adapter = categoryAdapter

        binding.currencyListView.setOnItemClickListener { _, _, position, _ ->
            val selectedString = (categoryAdapter.getItem(position) as String)
            categorySelected?.invoke(selectedString)
            dismiss()
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        dialog!!.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog!!.window!!.setBackgroundDrawableResource(R.drawable.popup_rounded_top_light_green)
        dialog!!.window!!.attributes.windowAnimations = R.style.ListDialogAnimations

        initListener()


    }

    private fun initListener() {
        binding.closeDialogButton.setOnClickListener { dismiss() }
        binding.floatingActionButton.setOnClickListener {
            val dialog = CreateCateogryDialog()
            dialog.show(requireActivity().supportFragmentManager, "create_cateogry_dialog")
        }

        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("tag","textChanged $newText")
                performSearchFunctionality(newText)
                return false
            }

        })
        binding.categorySearchEditText.addTextChangedListener {
            val filter = binding.categorySearchEditText.text.toString()
            categoryAdapter.setFilter(filter)
        }

        val closeButton: View? =
            binding.search.findViewById(androidx.appcompat.R.id.search_close_btn)
        closeButton?.setOnClickListener {
            binding.search.setQuery("", false);
            binding.search.isIconified = true;
        }

    }

    private fun performSearchFunctionality(newText: String?) {
        val filter = binding.categorySearchEditText.text.toString()
        categoryAdapter.setFilter(filter)
    }
}