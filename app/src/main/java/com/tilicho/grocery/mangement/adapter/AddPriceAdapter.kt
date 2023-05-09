package com.tilicho.grocery.mangement.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.AddPriceItemBinding
import com.tilicho.grocery.mangement.utils.AppConstants
import com.tilicho.grocery.mangement.utils.AppConstants.Companion.LOCAL_CURRENCY_SYMBOL
import com.tilicho.grocery.mangement.utils.ListItemModel
import com.tilicho.grocery.mangement.utils.PurchaseHistoryModel
import com.tilicho.grocery.mangement.viewModel.ListsViewModel
import java.util.*

class AddPriceAdapter(
    requireContext: Context,
    selectedListItemsList: MutableList<ListItemModel>,
    createNewPurchaseHistory: MutableList<PurchaseHistoryModel>,
    listsViewModel: ListsViewModel
) :
    RecyclerView.Adapter<AddPriceAdapter.ViewHolder>() {

    val mSelectedListItemsList = selectedListItemsList
    val mCreateNewPurchaseHistory = createNewPurchaseHistory
    val mListViewModel = listsViewModel

    var onPriceEdited: ((String, String) -> (Unit))? = null


    class ViewHolder(val binding: AddPriceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<AddPriceItemBinding>(
            inflater,
            R.layout.add_price_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mCreateNewPurchaseHistory.get(position)

        holder.binding.itemTitle.text = item.itemName ?: ""
        val unit =
            mListViewModel.units.value?.values?.filter { it.id == item.unitId }?.get(0)?.name ?: ""
        holder.binding.avaiableQuantityTextView.text =
            "${item.quantity.toInt()} * ${item.packageSize.toInt()} $unit"

        holder.binding.currencySymbol.text = LOCAL_CURRENCY_SYMBOL

        holder.binding.priceEditTextField.addTextChangedListener {
            if (it != null && it.toString().isNotEmpty() && it.toString()
                    .lowercase(Locale.ENGLISH) != "Price".lowercase(
                    Locale.ENGLISH
                )
            ) {
                onPriceEdited?.invoke(item.id, it.toString())
            }
        }

    }

    override fun getItemCount(): Int {
        return mCreateNewPurchaseHistory.size
    }
}