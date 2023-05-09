package com.tilicho.grocery.mangement.fragments

import android.content.Context
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.activities.HomeActivity
import com.tilicho.grocery.mangement.databinding.FragmentListsBinding
import com.tilicho.grocery.mangement.dialog.CreateListDialog
import com.tilicho.grocery.mangement.utils.AppConstants
import com.tilicho.grocery.mangement.utils.ListItemModel
import com.tilicho.grocery.mangement.viewModel.AppViewModelStore
import com.tilicho.grocery.mangement.viewModel.ListsViewModel
import java.util.*

class ListsFragment : Fragment() {

    private lateinit var binding: FragmentListsBinding
    private val appViewModelStore by lazy { AppViewModelStore.getInstance(requireActivity().application) }
    private val listsViewModel by lazy { appViewModelStore.getAndroidViewModel<ListsViewModel>() }

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
        AlertDialog.Builder(requireContext()).setCancelable(false).apply {
            setMessage(resources.getString(R.string.close_app_confirmation_message))
            setPositiveButton("Yes") { d, _ ->
                d.dismiss()
                activity?.finishAffinity()
            }
            setNegativeButton("No") { d, _ ->
                d.dismiss()
            }
        }.show()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_lists, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        //dialog = ProgressDialog(context)
        super.onActivityCreated(savedInstanceState)
        getLocalCurrency()
        binding.listViewModel = listsViewModel
        initListeners()
        addObservers()
        initTabLayout()
    }

    private fun addObservers() {
        listsViewModel.shouldShowEmptyUI.observe(viewLifecycleOwner) {

            if (!it) {

            }
        }

        listsViewModel.lists.observe(viewLifecycleOwner) {
            if (it != null && it.isNotEmpty()) {

                listsViewModel.setUIState(false)

            } else {
                listsViewModel.setUIState(true)
            }
        }

       /* listsViewModel.listItems.observe(viewLifecycleOwner) {

            prepareCurrentAndHistoryLists(true)

        }*/
    }

    private fun prepareCurrentAndHistoryLists(isListItemsObserved: Boolean = false) {
        //   try {
        if (listsViewModel.lists.value != null && (isListItemsObserved || listsViewModel.listItems.value != null)) {

            listsViewModel.lists.value?.values?.forEach { it ->
                if (listsViewModel.listItems.value != null) {
                    val listItems =
                        listsViewModel.listItems.value!!.get(it?.id)?.values
                    if (listItems != null && listItems.isNotEmpty()) {
                        val listOfPurchasedItems =
                            listItems.filter { it.purchased } as MutableList<ListItemModel>
                        if (listOfPurchasedItems != null && listOfPurchasedItems.isNotEmpty() && listOfPurchasedItems.size == listItems.size) {
                            listsViewModel._historyLists.value?.put(it.id, it)
                        } else {
                            listsViewModel._currentLists.value?.put(it.id, it)
                        }
                    } else {
                        listsViewModel._currentLists.value?.put(it.id, it)
                    }
                } else{
                    listsViewModel._currentLists.value?.put(it.id, it)
                }
            }
        }
        /*} catch (e: Exception) {
            e.printStackTrace()
        }*/
    }

    private fun initTabLayout() {
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText("My Lists")
        )
        binding.tabLayout.addTab(
            binding.tabLayout.newTab().setText("History")
        )

        binding.tabLayout.tabGravity = TabLayout.GRAVITY_FILL
        val adapter = MyAdapter(
            requireContext(), childFragmentManager,
            binding.tabLayout.tabCount
        )

        binding.viewPager.adapter = adapter
        binding.viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout))
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //("Not yet implemented")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                //("Not yet implemented")
            }

        })
    }

    private fun getLocalCurrency() {

        val timeZone = TimeZone.getDefault()
            .getDisplayName(false, 0)

        val tm = requireActivity().getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        try {
            AppConstants.LOCAL_CURRENCY =
                Currency.getInstance(Locale("", tm.networkCountryIso)).currencyCode.toString()
            AppConstants.LOCAL_CURRENCY_SYMBOL =
                Currency.getInstance(Locale("", tm.networkCountryIso)).symbol.toString()

        } catch (e: IllegalArgumentException) {

            val currency = Currency.getInstance(Locale.getDefault())
            AppConstants.LOCAL_CURRENCY = currency.currencyCode
            AppConstants.LOCAL_CURRENCY_SYMBOL = currency.symbol
        }


        Log.d("tag", timeZone)
        Log.d("tag", AppConstants.LOCAL_CURRENCY)
        Log.d("tag", AppConstants.LOCAL_CURRENCY_SYMBOL)
    }

    private fun initListeners() {
        binding.createListButton.setOnClickListener { showCreateListDialog() }
        binding.listsTitle.setOnClickListener { listsViewModel.setUIState(!listsViewModel.shouldShowEmptyUI.value!!) }
        binding.floatingActionButton.setOnClickListener { showCreateListDialog() }
    }

    private fun showCreateListDialog() {
        val dialog = CreateListDialog()
        dialog.show(requireActivity().supportFragmentManager, "create_list_dialog")
    }
}

@Suppress("DEPRECATION")
internal class MyAdapter(
    var context: Context,
    fm: FragmentManager,
    var totalTabs: Int,
) :
    FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                MyListsFragment()
            }
            1 -> {

                HistoryListFragment()
            }

            else -> getItem(position)
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }




}