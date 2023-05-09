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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.adapter.CategoryChooseAdapter
import com.tilicho.grocery.mangement.databinding.CategoryDialogLayoutBinding
import com.tilicho.grocery.mangement.utils.CategoryModel
import com.tilicho.grocery.mangement.viewModel.ListsViewModel

class CategoryDialog(selectedCategory: CategoryModel?) : DialogFragment() {

    private lateinit var binding: CategoryDialogLayoutBinding


    val previouslySelectedCategoty = selectedCategory
    var categorySelected: ((CategoryModel) -> Unit)? = null
    private lateinit var listViewModel: ListsViewModel

    private lateinit var categoryAdapter: CategoryChooseAdapter
    var listOfCategories = mutableListOf<CategoryModel>()
    /*{

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

       if (listViewModel.categories.value != null && listViewModel.categories.value!!.isNotEmpty()) {
           listOfCategories = ArrayList(listViewModel.categories.value!!.values)
       }
   }*/

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.category_dialog_layout, null, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initDialogWindow()
        initViewModel()
        initListener()
        addObserver()
    }

    private fun initViewModel() {
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

    private fun populateListView() {
        categoryAdapter = CategoryChooseAdapter(requireContext(), listOfCategories,previouslySelectedCategoty)
        binding.currencyListView.adapter = categoryAdapter
        binding.currencyListView.setOnItemClickListener { _, _, position, _ ->
            val selectedString = (categoryAdapter.getItem(position) as CategoryModel)
            categorySelected?.invoke(selectedString)
            dismiss()
        }
    }

    private fun addObserver() {
        listViewModel.categories.observe(viewLifecycleOwner) {
            if (listViewModel.categories.value != null && listViewModel.categories.value!!.isNotEmpty()) {
                listOfCategories = ArrayList(listViewModel.categories.value!!.values)
                populateListView()
            }
        }
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
                Log.d("tag", "textChanged $newText")
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
        // val filter = binding.categorySearchEditText.text.toString()
        categoryAdapter.setFilter(newText ?: "")
    }

    private fun initDialogWindow() {

        dialog!!.window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        dialog!!.window!!.setBackgroundDrawableResource(R.drawable.popup_rounded_top_light_green)
        dialog!!.window!!.attributes.windowAnimations = R.style.ListDialogAnimations
    }
}