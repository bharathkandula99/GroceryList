package com.tilicho.grocery.mangement.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.GroceryListItemBinding
import com.tilicho.grocery.mangement.utils.ListItemModel
import com.tilicho.grocery.mangement.viewModel.ListsViewModel

class GroceryListAdapter(
    applicationContext: Context,
    listOfNonPurchased: MutableList<ListItemModel>,
    listsViewModel: ListsViewModel
) :
    RecyclerView.Adapter<GroceryListAdapter.ViewHolder>() {

    var onCheckBoxClicked: ((Int) -> (Unit))? = null
    var onItemClicked: ((ListItemModel) -> (Unit))? = null
    val listItems = listOfNonPurchased
    val mListViewModel = listsViewModel


    class ViewHolder(val binding: GroceryListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<GroceryListItemBinding>(
            inflater,
            R.layout.grocery_list_item,
            parent,
            false
        )
        return ViewHolder(binding)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //  holder.binding.itemLayout.setOnClickListener { onCardClicked?.invoke(position) }
        val listItem = listItems.get(position)
        holder.binding.itemTitle.text = listItem.itemName
        val unit =
            mListViewModel.units.value?.values?.filter { it.id == listItem.unitId }?.get(0)?.name
        holder.binding.avaiableQuantityTextView.text =
            "${listItem.quantity.toInt()} * ${listItem.packageSize.toInt()} $unit"
        holder.binding.checkbox.setOnClickListener { onCheckBoxClicked?.invoke(position) }
        holder.binding.itemLayout.setOnClickListener { onItemClicked?.invoke(listItems.get(position)) }
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

}