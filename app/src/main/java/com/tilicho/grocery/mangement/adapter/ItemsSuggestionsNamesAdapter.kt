package com.tilicho.grocery.mangement.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import androidx.databinding.DataBindingUtil
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.ItemAutofillSuggestionBinding
import com.tilicho.grocery.mangement.utils.ItemModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ItemsSuggestionsNamesAdapter(context: Context, items: MutableList<ItemModel>? = null) :
    ArrayAdapter<ItemModel>(context, 0) {
    private val itemSuggestions = items
    private var filteredItemSuggestions: List<ItemModel>? = listOf()

    init {
        val uiScope = CoroutineScope(Dispatchers.Main)
        uiScope.launch {

            if (itemSuggestions != null) {
                filteredItemSuggestions = itemSuggestions
            }
            notifyDataSetChanged()
        }
    }


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // return super.getView(position, convertView, parent)

        val inflater = LayoutInflater.from(parent.context)
        val binding = if (convertView?.tag != null)
            convertView.tag as ItemAutofillSuggestionBinding
        else
            DataBindingUtil.inflate(inflater, R.layout.item_autofill_suggestion, parent, false)

        binding.root.tag = binding
        binding.itemName = filteredItemSuggestions?.get(position)?.name
        return binding.root

    }

    override fun getItem(position: Int): ItemModel? {
        return filteredItemSuggestions?.get(position)
    }

    override fun getFilter(): Filter {
        return itemFilter
    }

    override fun getCount(): Int {
        try {
            return filteredItemSuggestions?.count()!!
        } catch (e:Exception){
            e.printStackTrace()
            return 0
        }

    }

    private val itemFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filterString = constraint?.toString()?.trim()?.toLowerCase(Locale.ENGLISH) ?: ""

            val suggestions = if (filterString.isEmpty())
                itemSuggestions
            else
                itemSuggestions?.filter { s ->
                    s.name.toLowerCase(Locale.ENGLISH).contains(filterString)
                }

            return FilterResults().apply {
                values = suggestions
                count = suggestions?.count()!!
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            filteredItemSuggestions = if (results?.values == null)
                itemSuggestions
            else (results.values as? List<ItemModel>) ?: itemSuggestions

            clear()

            notifyDataSetChanged()
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as? ItemModel)?.name ?: ""
        }
    }

}