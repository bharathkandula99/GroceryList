package com.tilicho.grocery.mangement.dialog

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.updatePadding
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.textview.MaterialTextView
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.adapter.ItemsSuggestionsNamesAdapter
import com.tilicho.grocery.mangement.databinding.AddItemDialogBinding
import com.tilicho.grocery.mangement.sharedPreferences.AppPreff
import com.tilicho.grocery.mangement.utils.*
import com.tilicho.grocery.mangement.viewModel.ListsViewModel
import java.util.*
import kotlin.collections.ArrayList

class AddItemDialog(
    context: Context,
    activity: FragmentActivity,
    selectedListValue: ListsModel?,
    val isEditFlow: Boolean = false,
    val selectedListItem: ListItemModel? = null
) : DialogFragment()/*, AdapterView.OnItemClickListener*/ {

    private lateinit var binding: AddItemDialogBinding
    private var rootBottomPadding = 0

    private var selectedCategory: CategoryModel? = null
    var selectedUnit: UnitModel? = null
    var selectedItem: ItemModel? = null
    var itemName: String? = null
    var quantity: String? = null
    var mPackage: String? = null

    private lateinit var listViewModel: ListsViewModel
    val selectedList = selectedListValue

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        initUnitsSpinner()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initDialogWindow()
        initViewModel()
        addObservers()
        initListeners()
        initEditFlowDialog()
    }

    private fun addObservers() {

        listViewModel.items.observe(viewLifecycleOwner) {
            if (listViewModel.items.value != null && listViewModel.items.value!!.isNotEmpty()) {
                initAutofillSuggestions(ArrayList(listViewModel.items.value!!.values))
            }
        }

        listViewModel.units.observe(viewLifecycleOwner) {
            if (listViewModel.units.value != null && listViewModel.units.value!!.isNotEmpty()) {

            }
        }

    }

    private fun initListeners() {
        binding.closeDialogButton.setOnClickListener { dismiss() }
        binding.addItemButton.setOnClickListener {
            createListItem()
        }
        binding.updateItemButton.setOnClickListener {
            updateListItem()
        }
        binding.cateogryButton.setOnClickListener {
            val dialog = CategoryDialog(selectedCategory).apply {
                categorySelected = {
                    selectedCategory = it
                    binding.cateogryButton.text = it.name ?: ""
                    validateAddItemButtonStatus()
                }
            }
            activity?.let { it1 ->
                dialog.show(
                    it1.supportFragmentManager,
                    "currency_chooser_dialog"
                )
            }
        }
        binding.itemNameEditText.addTextChangedListener {
            if (it != null && it.toString().isNotEmpty()) {
                itemName = it.toString()
            } else{
                itemName = ""
            }
            if (!(selectedItem != null && it.toString()
                    .lowercase(Locale.ENGLISH) == selectedItem?.name?.lowercase(
                    Locale.ENGLISH
                ))
            ) {
                selectedItem = null
            }
            validateAddItemButtonStatus()
        }
        binding.packageEditText.addTextChangedListener {
            if (it != null && it.toString().isNotEmpty()) {
                mPackage = it.toString()
            } else{
                mPackage = ""
            }
            validateAddItemButtonStatus()
        }
        binding.quantityEditText.addTextChangedListener {
            if (it != null && it.toString().isNotEmpty()) {
                quantity = it.toString()
            } else{
                quantity = ""
            }
            validateAddItemButtonStatus()
        }
        listViewModel.apply {
            isCreateListItemSucess = { b: Boolean, s: String ->
                hideProgressBar()
                if (b) {
                    dismiss()
                } else {
                    Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initEditFlowDialog() {
        if (isEditFlow) {
            binding.isEditFlow = true
            prefillEditFlowDialog(selectedListItem)
        } else{
            binding.createNewListTitleTextView.text = "Add Item"
        }
    }

    private fun prefillEditFlowDialog(selectedListItem: ListItemModel?) {
        binding.itemNameEditText.text =
            Editable.Factory.getInstance().newEditable(selectedListItem?.itemName.toString() ?: "")
        itemName = selectedListItem?.itemName.toString() ?: ""
        fillCategoryBasedOnCategoryId(selectedListItem?.categoryId ?: "")
        fillUnitBasedOnUnitId(selectedListItem?.unitId ?: "")
        binding.quantityEditText.text =
            Editable.Factory.getInstance().newEditable(selectedListItem?.quantity.toString() ?: "")
        binding.packageEditText.text = Editable.Factory.getInstance()
            .newEditable(selectedListItem?.packageSize.toString() ?: "")
        quantity = selectedListItem?.quantity.toString() ?: ""
        mPackage = selectedListItem?.packageSize.toString() ?: ""
        binding.createNewListTitleTextView.text = "Edit Item"
    }

    private fun initDialogWindow() {
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

    private fun initUnitsSpinner() {
        val packageUnitsAdapter = ArrayAdapter.createFromResource(
            requireContext(), R.array.units,
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
                prepareSelectedUnitWithCharSequence((view as MaterialTextView).text)
            }
        }
        prepareSelectedUnitWithCharSequence("kg")
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

    private fun initAutofillSuggestions(itemsList: ArrayList<ItemModel>) {
        val itemsSuggestionsNamesAdapter = ItemsSuggestionsNamesAdapter(requireContext(), itemsList)
        binding.itemNameEditText.setAdapter(itemsSuggestionsNamesAdapter)
        binding.itemNameEditText.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = itemsSuggestionsNamesAdapter.getItem(position)
            if (selectedItem != null) {
                this.selectedItem = selectedItem
                fillFieldsBasedOnSelectedItem(selectedItem)
                Toast.makeText(context, selectedItem.name ?: "", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "suggestion.name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun prepareSelectedUnitWithCharSequence(text: CharSequence) {
        try {
            selectedUnit =
                listViewModel.units.value?.values?.filter { it.name.lowercase(Locale.ENGLISH) == text.toString().lowercase(Locale.ENGLISH) }?.get(0)
            validateAddItemButtonStatus()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun fillCategoryBasedOnCategoryId(categoryId: String) {
        selectedCategory =
            listViewModel.categories.value?.values?.filter { it.id == categoryId }
                ?.get(0)
        binding.cateogryButton.text = selectedCategory?.name ?: ""
    }

    private fun fillUnitBasedOnUnitId(unitId: String) {
        selectedUnit = listViewModel.units.value?.values?.filter { it.id == unitId }
            ?.get(0)
        binding.unitsSpinner.setSelection(
            resources.getStringArray(R.array.units).toList()
                .indexOf(selectedUnit?.name)
        )
    }

    private fun fillFieldsBasedOnSelectedItem(selectedItem: ItemModel) {
        itemName = selectedItem.name
        fillCategoryBasedOnCategoryId(selectedItem.categoryId)
        fillUnitBasedOnUnitId(selectedItem.unitId)
        validateAddItemButtonStatus()
    }

    private fun validateAddItemButtonStatus() {

        val isEnable = ((mPackage != null && mPackage?.isNotEmpty()!!)
                && (quantity != null && quantity?.isNotEmpty()!!)
                && (selectedCategory != null && selectedCategory?.id?.isNotEmpty()!!)
                && (selectedUnit != null && selectedUnit?.id?.isNotEmpty()!!)
                && (itemName != null && itemName?.isNotEmpty()!!))
        binding.addItemButton.isEnabled = isEnable
        binding.updateItemButton.isEnabled = isEnable


    }

    private fun createListItem() {
        showprogressBar()
        val newListItemId = UUID.randomUUID().toString()
        var shouldCreateNewItem: Boolean = true
        var itemId = ""
        if (selectedItem != null && selectedItem?.categoryId == selectedCategory?.id && selectedItem?.unitId == selectedUnit?.id) {
            itemId = selectedItem!!.id
            shouldCreateNewItem = false
        } else {
            itemId = UUID.randomUUID().toString()
        }
        val newListItem = ListItemModel(
            newListItemId,
            itemId,
            selectedList?.id!!,
            AppPreff(requireContext()).getUserID().toString(),
            quantity?.toDouble()!!,
            mPackage?.toDouble()!!,
            false,
            0,
            System.currentTimeMillis(),
            System.currentTimeMillis(),
            false,
            selectedUnit?.id ?: "",
            selectedCategory?.id ?: "",
            itemName?.toString()!!
        )
        listViewModel.createNewListItem(newListItem, shouldCreateNewItem)
    }

    private fun updateListItem() {
        showprogressBar()
        try {
            if (listViewModel.items.value != null) {
                selectedItem = listViewModel.items.value?.values?.filter {
                    it.name.lowercase(Locale.ENGLISH) == itemName?.lowercase(
                        Locale.ENGLISH
                    ) && it.categoryId == selectedCategory?.id && it.unitId == selectedUnit?.id
                }?.get(0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


        var itemId = ""
        var shouldCreateNewItem = true

        if (selectedCategory?.id == selectedListItem?.categoryId && selectedUnit?.id == selectedListItem?.unitId && itemName?.lowercase(
                Locale.ENGLISH
            ) == selectedListItem?.itemName
        ) {
            itemId = selectedListItem?.itemId.toString()
            shouldCreateNewItem = false
        } else if (selectedItem != null && selectedItem?.name == itemName.toString() && selectedItem?.categoryId == selectedCategory?.id && selectedItem?.unitId == selectedUnit?.id) {
            itemId = selectedItem?.id.toString()
            shouldCreateNewItem = false
        } else {
            itemId = UUID.randomUUID().toString()
            shouldCreateNewItem = true
        }

        val updateListItem = ListItemModel(
            selectedListItem?.id ?: "",
            itemId,
            selectedList?.id ?: "",
            AppPreff(requireContext()).getUserID().toString(),
            quantity?.toDouble()!!,
            mPackage?.toDouble()!!,
            false,
            0,
            selectedListItem?.createdAt ?: System.currentTimeMillis(),
            System.currentTimeMillis(),
            false,
            selectedUnit?.id ?: "",
            selectedCategory?.id ?: "",
            itemName!!
        )
        listViewModel.updateListItem(updateListItem, shouldCreateNewItem)


    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        dialog!!.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun showprogressBar() {
        binding.progressBar.visibility = View.VISIBLE
        dialog!!.window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        );

    }

    private fun configureInset() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            binding.root.setOnApplyWindowInsetsListener { v, insets ->
                v.updatePadding(bottom = rootBottomPadding + insets.systemWindowInsetBottom)
                insets
            }
        }
    }

    /*override fun onDismiss(dialog: DialogInterface) {
       super.onDismiss(dialog)
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (imm.isAcceptingText) {
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 1)
        } else{
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
        }
    }*/

    override fun getTheme(): Int = R.style.AppTheme_NoWiredStrapInNavigationBar
}