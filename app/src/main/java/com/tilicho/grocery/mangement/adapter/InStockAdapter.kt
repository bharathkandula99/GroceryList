package com.tilicho.grocery.mangement.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.InStockItemBinding
import com.tilicho.grocery.mangement.utils.ItemConsumptions
import com.tilicho.grocery.mangement.utils.ListItemModel
import com.tilicho.grocery.mangement.viewModel.ListsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class InStockAdapter(
    applicationContext: Context,
    listOfListItems: MutableList<ListItemModel>,
    listsViewModel: ListsViewModel
) :
    RecyclerView.Adapter<InStockAdapter.ViewHolder>() {
    val mContext = applicationContext

    var onCardClicked: ((ListItemModel) -> (Unit))? = null
    val list = listOfListItems
    val mListViewModel = listsViewModel
    private var filter = ""
    private var filteredListItemList: List<ListItemModel>? = null

    init {
        val uiScope = CoroutineScope(Dispatchers.Main)
        uiScope.launch {

            if (list != null) {
                filteredListItemList = list
            }
            notifyDataSetChanged()
        }
    }


    class ViewHolder(val binding: InStockItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<InStockItemBinding>(
            inflater,
            R.layout.in_stock_item,
            parent,
            false
        )
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.itemTitle.text = filteredListItemList?.get(position)?.itemName
        val unit =
            mListViewModel.units.value?.values?.filter { it.id == filteredListItemList?.get(position)?.unitId }
                ?.get(0)?.name
        var itemConsumptions = ItemConsumptions()
        try {
            itemConsumptions =
                mListViewModel.consumptions.value?.get(filteredListItemList?.get(position)?.itemId)?.values?.filter {
                    it.itemId == filteredListItemList?.get(
                        position
                    )?.itemId
                }
                    ?.get(0)!!

        } catch (e: Exception) {
            e.printStackTrace()
        }

        var avaiableQuantity = try {
            if (itemConsumptions != null && itemConsumptions.availableQuantity != null) {
                itemConsumptions?.availableQuantity.toString()
            } else {
                filteredListItemList?.get(position)?.quantity.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            filteredListItemList?.get(position)?.quantity.toString()
        }
        try {
            val item = mListViewModel.items.value?.values?.filter {
                it.id == filteredListItemList?.get(position)?.itemId
            }
                ?.get(0)
            if (item != null) {
                if (item.minimumQuantity >= itemConsumptions?.availableQuantity && !(itemConsumptions.availableQuantity <= 0.0 || itemConsumptions.availableQuantity <= 0)) {
                    holder.binding.avaiableQuantityTextView.text = "Minimum quantity reached"
                    holder.binding.avaiableQuantityTextView.setTextColor(Color.parseColor("#FFA500"))

                } else if (itemConsumptions.availableQuantity <= 0.0 || itemConsumptions.availableQuantity <= 0) {
                    holder.binding.avaiableQuantityTextView.text = "Out of stock"
                    val color: Int = R.color.disable_foreground
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        holder.binding.itemLayout.setForeground(ColorDrawable(ContextCompat.getColor(mContext, color)))
                    }

                    holder.binding.avaiableQuantityTextView.setTextColor(Color.parseColor("#F44336"))
                } else {
                    holder.binding.avaiableQuantityTextView.text =
                        "${avaiableQuantity} * ${filteredListItemList?.get(position)?.packageSize?.toInt()} $unit"

                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            holder.binding.avaiableQuantityTextView.text =
                "${avaiableQuantity} * ${filteredListItemList?.get(position)?.packageSize?.toInt()} $unit"
            holder.binding.itemLayout.isEnabled = true
            holder.binding.itemLayout.isClickable = true
        }


        //holder.binding.avaiableQuantityTextView.text = (list.get(position).quantity * list.get(position).packageSize).toString()
        holder.binding.itemLayout.setOnClickListener { onCardClicked?.invoke(list.get(position)) }

    }

    fun setFilter(filterString: String) {
        filter = filterString.trim().toLowerCase(Locale.ENGLISH)

        if (list == null)
            return

        filteredListItemList = list!!.filter { c ->
            c.itemName.lowercase(Locale.ENGLISH).contains(filter)
        }

        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        try {
            return filteredListItemList?.size ?: 0
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
    }

}