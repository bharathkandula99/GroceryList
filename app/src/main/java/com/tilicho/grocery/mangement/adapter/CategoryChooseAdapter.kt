package com.tilicho.grocery.mangement.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.CategoryItemBinding
import com.tilicho.grocery.mangement.utils.CategoryModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class CategoryChooseAdapter(
    applicationContext: Context,
    categories: MutableList<CategoryModel>? = null,
    previouslyChosenCategory: CategoryModel? = null
) : BaseAdapter() {

    val categoriesList = categories
    val previouslyChosenCategory = previouslyChosenCategory
    private var filter = ""
    private var filteredCategoryList: List<CategoryModel>? = null

    init {
        val uiScope = CoroutineScope(Dispatchers.Main)
        uiScope.launch {

            if (categoriesList != null) {
                filteredCategoryList = categoriesList
            }
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
        binding.categoryNameTextView.text = filteredCategoryList!![position].name
        try {
            binding.isPreviouslyChosen =
                previouslyChosenCategory != null && previouslyChosenCategory.id == filteredCategoryList!![position].id
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return binding.root
    }

    fun setFilter(filterString: String) {
        filter = filterString.trim().toLowerCase(Locale.ENGLISH)

        if (categoriesList == null)
            return

        filteredCategoryList = categoriesList!!.filter { c ->
            c.name.lowercase(Locale.ENGLISH).contains(filter)
        }

        notifyDataSetChanged()
    }
}