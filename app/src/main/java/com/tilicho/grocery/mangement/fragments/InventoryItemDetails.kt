package com.tilicho.grocery.mangement.fragments

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.FragmentInventoryItemDetailsBinding
import com.tilicho.grocery.mangement.utils.ItemConsumptions
import com.tilicho.grocery.mangement.utils.ItemModel
import com.tilicho.grocery.mangement.utils.ListItemModel
import com.tilicho.grocery.mangement.viewModel.AppViewModelStore
import com.tilicho.grocery.mangement.viewModel.InventoryViewModel
import com.tilicho.grocery.mangement.viewModel.ListsViewModel
import java.util.*
import kotlin.math.roundToInt


class InventoryItemDetails : Fragment() {

    private lateinit var binding: FragmentInventoryItemDetailsBinding
    private val appViewModelStore by lazy { AppViewModelStore.getInstance(requireActivity().application) }
    private val inventoryViewModel by lazy { appViewModelStore.getAndroidViewModel<InventoryViewModel>() }
    private val listsViewModel by lazy { appViewModelStore.getAndroidViewModel<ListsViewModel>() }

    private var selectedListItemModel: ListItemModel? = null
    private var item: ItemModel? = null
    private var itemConsumptions: ItemConsumptions? = null
    var minimumQuantity: Double = 0.0
    var quantityConsumed: Double = 0.0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_inventory_item_details, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button even
                    saveDetails()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (arguments?.get("selectedListItemModel") != null) {
            selectedListItemModel =
                (arguments?.get("selectedListItemModel") as MutableList<ListItemModel>)[0]
        }
        /*if (arguments?.get("listItemModel") != null) {
            selectedListItemModel = (arguments?.get("listItemModel") as ListItemModel)
        }*/
        binding.inventoryViewModel = inventoryViewModel
        addObservers()
        initLitiners()
        populateUI()


    }

    private fun addObservers() {
        listsViewModel.apply {
            isInventoryDetailsUpdationSuccess = { b: Boolean, s: String ->
                hideProgressBar()
                if (!b) {
                    Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show()
                } else {
                    binding.root.findNavController().navigateUp()
                }
            }
        }
    }

    private fun populateUI() {
        binding.roundTextView.text =
            getTwoDigitName(selectedListItemModel?.itemName ?: "").toUpperCase(Locale.ENGLISH)
        binding.itemName.text = selectedListItemModel?.itemName ?: ""
        binding.listName.text =
            listsViewModel.lists.value?.values?.filter { it.id == selectedListItemModel?.listId }
                ?.get(0)?.name ?: ""

        val unit =
            listsViewModel.units.value?.values?.filter { it.id == selectedListItemModel?.unitId }
                ?.get(0)?.name ?: ""
        binding.unitsText.text = "Units : $unit"
        binding.lastThirtyDays.text =
            "${selectedListItemModel?.quantity?.roundToInt()} * ${selectedListItemModel?.packageSize?.roundToInt()} $unit"


        /*      (selectedListItemModel?.quantity?.times(selectedListItemModel?.packageSize!!))?.toInt()
                  .toString() ?: ""*/
        item = listsViewModel.items.value?.values?.filter { it.id == selectedListItemModel?.itemId }
            ?.get(0)
        itemConsumptions =
            listsViewModel.consumptions.value?.get(item?.id)?.values?.filter { it.itemId == item?.id }
                ?.get(0)

        binding.quantityConsumedUnitText.text =
            " * ${selectedListItemModel?.packageSize?.roundToInt()} $unit"
        val consumed = itemConsumptions?.quantity?.minus(itemConsumptions?.availableQuantity!!)

        try {
            val percentage =
                ((itemConsumptions?.availableQuantity!!) / (itemConsumptions?.quantity!!)) * 100
            if (percentage > 12 && percentage <= 50) {
                binding.image.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.jarlow
                    )
                )
            } else if (percentage <= 12) {
                binding.image.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_jar_empty
                    )
                )
            } else if (percentage > 50 && percentage <= 90) {
                binding.image.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.jarmedium
                    )
                )
            } else {
                binding.image.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.jarfull
                    )
                )
            }
        } catch (e: Exception) {

            e.printStackTrace()
            binding.image.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.jarfull
                )
            )


        }



        quantityConsumed = consumed ?: 0.0
        binding.consumedEditTextField.text =
            Editable.Factory.getInstance().newEditable(consumed.toString() ?: "")

        if (item?.minimumQuantity != null && item?.minimumQuantity!! <= 0) {
            binding.setButton.visibility = View.VISIBLE
            binding.minimumQuantityTextField.visibility = View.GONE
        } else {
            binding.setButton.visibility = View.GONE
            binding.minimumQuantityTextField.visibility = View.VISIBLE
            binding.minimumQuantityEditText.text =
                Editable.Factory.getInstance().newEditable(item?.minimumQuantity.toString() ?: "")
        }
        minimumQuantity = item?.minimumQuantity ?: 0.0

    }

    private fun initLitiners() {

        binding.menu.setOnClickListener {

            //  setHasOptionsMenu(true)

        }
        binding.minimumQuantityEditText.addTextChangedListener {
            if (it != null && it.toString() != null && it.toString().isNotEmpty()) {
                minimumQuantity = it.toString().toDouble()
            }
        }
        binding.consumedEditTextField.addTextChangedListener {
            if (it != null && it.toString() != null && it.toString().isNotEmpty()) {
                quantityConsumed = it.toString().toDouble()
            }
        }

        binding.setButton.setOnClickListener {
            binding.setButton.visibility = View.GONE
            binding.minimumQuantityTextField.visibility = View.VISIBLE

            binding.minimumQuantityEditText.text =
                Editable.Factory.getInstance().newEditable(0.0.toString() ?: "")
        }
        binding.backArrow.setOnClickListener {
            //onBackPressed()
            saveDetails()
            //binding.root.findNavController().navigateUp()
        }
    }

    private fun saveDetails() {
        try {
            if (validateFields()) {

                val purchasehistoryId =
                    listsViewModel.purchaseHistory.value?.get(selectedListItemModel?.listId)
                        ?.get(item?.id)?.values?.filter { it.itemId == item?.id }?.get(0)?.id
                val listId = selectedListItemModel?.listId
                val itemId = item?.id
                val userId = item?.userId
                val itemConsumptionsId = itemConsumptions?.id
                val avaiableQuantity =
                    itemConsumptions?.quantity?.minus(quantityConsumed)
                        ?: itemConsumptions?.quantity
                        ?: 0.0
                val updates: MutableMap<String, Any> = HashMap()
                updates["items/$userId/$itemId/minimumQuantity"] = minimumQuantity ?: 0.0
                updates["itemConsumptions/$userId/$itemId/$itemConsumptionsId/availableQuantity"] =
                    avaiableQuantity
                updates["itemConsumptions/$userId/$itemId/$itemConsumptionsId/updatedAt"] =
                    System.currentTimeMillis()
                updates["purchaseHistory/$userId/$listId/$itemId/$purchasehistoryId/availableQuantity"] =
                    avaiableQuantity
                updates["purchaseHistory/$userId/$listId/$itemId/$purchasehistoryId/updatedAt"] =
                    System.currentTimeMillis()
                listsViewModel.updateInventoryDetails(updates)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            binding.root.findNavController().navigateUp()
        }
    }

    private fun validateFields(): Boolean {
        try {
            if ((itemConsumptions?.quantity
                    ?: selectedListItemModel?.quantity)!! >= minimumQuantity
            ) {
                if ((itemConsumptions?.quantity
                        ?: selectedListItemModel?.quantity)!! >= quantityConsumed
                ) {

                    return true
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Minimum quantity is not greater than item quantity",
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Minimum quantity is not greater than item quantity",
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    fun getTwoDigitName(name: String): String {
        try {
            if (name != null && name.isNotEmpty()) {
                val names = name.trim().split(" ")
                return if (names.size == 1) {
                    when (name.length) {
                        1 -> {
                            name
                        }
                        0 -> {
                            ""
                        }
                        2 -> {
                            name
                        }
                        else -> {
                            name.substring(0, 2)
                        }
                    }
                } else {
                    names[0][0].toString() + names[1][0].toString()
                }
            } else {
                return ""
            }
        } catch (e: Exception) {
            return ""
        }
    }

    private fun onBackPressed() {
        Toast.makeText(requireContext(), "back pressed ", Toast.LENGTH_SHORT).show()
    }

    fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        requireActivity().window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    fun showprogressBar() {
        binding.progressBar.visibility = View.VISIBLE
        requireActivity().window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        );

    }

}