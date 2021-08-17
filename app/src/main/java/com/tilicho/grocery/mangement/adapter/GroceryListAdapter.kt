package com.tilicho.grocery.mangement.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.GroceryListItemBinding

class GroceryListAdapter(applicationContext: Context) :
    RecyclerView.Adapter<GroceryListAdapter.ViewHolder>() {

    var onCardClicked: ((Int) -> (Unit))? = null


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
        holder.binding.checkbox.setOnClickListener {onCardClicked?.invoke(position)  }

    }

    override fun getItemCount(): Int {
        return 4

    }

}