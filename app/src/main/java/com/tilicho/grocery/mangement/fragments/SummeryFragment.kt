package com.tilicho.grocery.mangement.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.activities.HomeActivity
import com.tilicho.grocery.mangement.adapter.WastageAdapter
import com.tilicho.grocery.mangement.databinding.FragmentSummeryBinding
import com.tilicho.grocery.mangement.dialog.AddWastageDialog
import com.tilicho.grocery.mangement.utils.FoodWastageModel
import com.tilicho.grocery.mangement.viewModel.AppViewModelStore
import com.tilicho.grocery.mangement.viewModel.FoodSummeryViewModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class SummeryFragment : Fragment() {

    private lateinit var binding: FragmentSummeryBinding
    private val appViewModelStore by lazy { AppViewModelStore.getInstance(requireActivity().application) }
    private val foodSummeryViewModel by lazy { appViewModelStore.getAndroidViewModel<FoodSummeryViewModel>() }
    var listOfWastage: ArrayList<FoodWastageModel> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    // Handle the back button even
                    handleBackPressed()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)


    }

    private fun handleBackPressed() {
        (activity as HomeActivity?)?.selectFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_summery, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        //dialog = ProgressDialog(context)
        super.onActivityCreated(savedInstanceState)
        binding.foodSummeryViewModel = foodSummeryViewModel

        addObservers()
        initLitiners()
    }

    private fun addObservers() {
        foodSummeryViewModel.wastages.observe(viewLifecycleOwner) { it ->
            if (it != null && it.isNotEmpty()) {
                val list = ArrayList(it.values)
                listOfWastage = list
                populateListsRecyclerView(list)
                foodSummeryViewModel.setUIState(false)
            } else {
                foodSummeryViewModel.setUIState(true)
            }

        }
    }

    private fun populateListsRecyclerView(list: ArrayList<FoodWastageModel>) {
        binding.wastageRecyclerView.adapter = WastageAdapter(
            list
        ).apply {
            onCardClicked = {
                performClipBoardCopy(it)
            }
        }
        binding.wastageRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())
    }

    private fun performClipBoardCopy(foodWastageModel: FoodWastageModel) {
        val string =
            "${foodWastageModel.wastage} kg's food wasted on ${getFormattedDate(foodWastageModel.createdAt)} ${foodWastageModel.mealType},which can serve ${(foodWastageModel.wastage / 1.3).roundToInt()} meals"
        copyToClipboard(string)
    }

    private fun initLitiners() {

        binding.share.setOnClickListener {
            preparestringAndShare()
        }

        binding.floatingActionButton.setOnClickListener {
            showAddWastageDialog()
        }

        binding.createListButton.setOnClickListener {
            showAddWastageDialog()
        }
    }

    private fun preparestringAndShare() {
        var wastage = 0.0
        listOfWastage.forEach { wastage += it.wastage }
        val string =
            "$wastage kg's food wasted in this month,which can serve which can serve ${(wastage / 1.3)} meals"

        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Grocery List")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, string)
        startActivity(Intent.createChooser(sharingIntent, "Share via"))
    }

    private fun showAddWastageDialog() {
        val dialog = AddWastageDialog()
        dialog.show(requireActivity().supportFragmentManager, "add_wastage_dialog")
    }

    private fun copyToClipboard(text: CharSequence) {
        val clipboard =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show()
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