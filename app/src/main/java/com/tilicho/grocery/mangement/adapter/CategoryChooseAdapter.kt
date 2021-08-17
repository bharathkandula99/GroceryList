package com.tilicho.grocery.mangement.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.CategoryItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class CategoryChooseAdapter(
    applicationContext: Context,
    private val previouslyChosenCategory: String? = null
) : BaseAdapter() {
    private var list = listOf(
        "Category1",
        "Category2",
        "Category3",
        "Category4",
        "Category5",
        "Category6",
        "Category7"
    )
    private var filter = ""
    private var filteredCategoryList: List<String>? = null

    init {
        val uiScope = CoroutineScope(Dispatchers.Main)
        uiScope.launch {

            filteredCategoryList = list.toList()
            notifyDataSetChanged()
        }
    }


    override fun getCount(): Int {
        return filteredCategoryList?.count() ?: 0

    }

    override fun getItem(position: Int): Any {

        return filteredCategoryList!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val inflater = LayoutInflater.from(parent!!.context)
        val binding = if (convertView?.tag != null) {
            convertView.tag as CategoryItemBinding
        } else {
            DataBindingUtil.inflate(inflater, R.layout.category_item, parent, false)
        }

        binding.root.tag = binding

        binding.categoryNameTextView.text = filteredCategoryList!![position]

        binding.isPreviouslyChosen = false

        return binding.root
    }

    fun setFilter(filterString: String) {
        filter = filterString.trim().toLowerCase(Locale.ENGLISH)

        if (list == null)
            return

        filteredCategoryList = list!!.filter { c ->
            c.lowercase(Locale.ENGLISH).startsWith(filter)
        }

        notifyDataSetChanged()
    }
}