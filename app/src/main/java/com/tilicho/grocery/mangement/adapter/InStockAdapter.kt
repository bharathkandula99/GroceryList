package com.tilicho.grocery.mangement.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.InStockItemBinding

class InStockAdapter(applicationContext: Context) :
    RecyclerView.Adapter<InStockAdapter.ViewHolder>() {

    var onCardClicked: ((Int) -> (Unit))? = null


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
        holder.binding.itemLayout.setOnClickListener { onCardClicked?.invoke(position) }

    }

    override fun getItemCount(): Int {
        return 10

    }

}