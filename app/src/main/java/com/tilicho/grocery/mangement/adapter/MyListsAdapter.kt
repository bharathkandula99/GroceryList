package com.tilicho.grocery.mangement.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.ListItemBinding
import com.tilicho.grocery.mangement.utils.ListItemModel
import com.tilicho.grocery.mangement.utils.ListsModel
import com.tilicho.grocery.mangement.viewModel.ListsViewModel
import java.text.SimpleDateFormat

class MyListsAdapter(
    applicationContext: Context,
    lists: List<ListsModel>,
    listsViewModel: ListsViewModel,
    isHistoryTab: Boolean = false
) :
    RecyclerView.Adapter<MyListsAdapter.ViewHolder>() {
    var onCardClicked: ((ListsModel) -> (Unit))? = null


    val activity = applicationContext as Activity
    val lists1 = lists as List<ListsModel>
    val mListViewModel = listsViewModel
    val mIsHistoryTab = isHistoryTab

    class ViewHolder(val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ListItemBinding>(
            inflater,
            R.layout.list_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.listTitle.text = lists1[position].name
        holder.binding.createdOn.text =
            SimpleDateFormat("dd MMM yyyy h:mma").format(lists1.get(position).updatedAt)
        holder.binding.cardView.setOnClickListener { onCardClicked?.invoke(lists1.get(position)) }

        if (mListViewModel.listItems.value != null && mListViewModel.listItems.value?.isNotEmpty()!!) {

            val listItems =
                mListViewModel.listItems.value!!.get(lists1[position].id)?.values
            if (listItems != null && listItems.isNotEmpty()) {
                val listOfPurchasedItems =
                    listItems.filter { it.purchased } as MutableList<ListItemModel>
                if (mIsHistoryTab) {
                    holder.binding.pendingItems.text =
                        "Items Bought : ${listOfPurchasedItems.size}"
                } else {
                    holder.binding.pendingItems.text =
                        "Pending Items : ${listOfPurchasedItems.size} / ${listItems.size}"
                }

            } else {
                if (mIsHistoryTab) {
                    holder.binding.pendingItems.text = "Items Bought : 0"
                } else {
                    holder.binding.pendingItems.text = "Pending Items : 0 / 0"
                }

            }
        } else {
            if (mIsHistoryTab) {
                holder.binding.pendingItems.text = "Items Bought : 0"
            } else {
                holder.binding.pendingItems.text = "Pending Items : 0 / 0"
            }
        }

    }

    override fun getItemCount(): Int {
        return lists1.size
    }
}