package com.tilicho.grocery.mangement.adapter

import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.WastageItemBinding
import com.tilicho.grocery.mangement.utils.FoodWastageModel
import com.tilicho.grocery.mangement.utils.ListsModel
import java.util.*
import kotlin.math.roundToInt

class WastageAdapter(list: ArrayList<FoodWastageModel>) :
    RecyclerView.Adapter<WastageAdapter.ViewHolder>() {
    val mList = list

    var onCardClicked: ((FoodWastageModel) -> (Unit))? = null

    class ViewHolder(val binding: WastageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<WastageItemBinding>(
            inflater,
            R.layout.wastage_item,
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val serveText = mList.get(position).wastage / 1.3

        holder.binding.quantityTextField.text =
            "Food wasted - ${mList.get(position).wastage}Kg's"
        holder.binding.serveTextField.text = "Which can serve ${serveText.roundToInt()} meals"
        holder.binding.dateTextField.text = getFormattedDate(mList.get(position).createdAt)
        holder.binding.parentLayout.setOnClickListener {
          onCardClicked?.invoke(mList.get(position))
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun getFormattedDate(millis: Long): String {
        val time: Calendar = Calendar.getInstance()
        time.timeInMillis = millis
        val now: Calendar = Calendar.getInstance()
        val timeFormatString = "dd MMM"
        val dateTimeFormatString = "dd MMM"
        val HOURS = (60 * 60 * 60).toLong()
        return if (now.get(Calendar.DATE) === time.get(Calendar.DATE)) {
            "Today "
        } else if (now.get(Calendar.DATE) - time.get(Calendar.DATE) === 1) {
            "Yesterday "
        } else if (now.get(Calendar.YEAR) === time.get(Calendar.YEAR)) {
            DateFormat.format(dateTimeFormatString, time).toString()
        } else {
            DateFormat.format("dd MMM", time).toString()
        }
    }
}