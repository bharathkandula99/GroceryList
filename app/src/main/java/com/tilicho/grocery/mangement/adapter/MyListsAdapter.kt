package com.tilicho.grocery.mangement.adapter

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.ListItemBinding

class MyListsAdapter(applicationContext: Context) :
    RecyclerView.Adapter<MyListsAdapter.ViewHolder>() {
    var onCardClicked:((String)->(Unit))?=null


    val activity = applicationContext as Activity

    class ViewHolder(val binding:ListItemBinding) :
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.createdOn.text = "Created on : 23/07/2021"
        holder.binding.cardView.setOnClickListener { onCardClicked?.invoke("clicked") }

    }

    override fun getItemCount(): Int {
        return 5
    }
}