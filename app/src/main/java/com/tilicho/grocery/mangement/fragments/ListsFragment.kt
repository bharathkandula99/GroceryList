package com.tilicho.grocery.mangement.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.google.android.material.tabs.TabLayout
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.FragmentListsBinding
import com.tilicho.grocery.mangement.databinding.FragmentSettingsBinding
import com.tilicho.grocery.mangement.dialog.AboutUsDialog
import com.tilicho.grocery.mangement.dialog.CreateListDialog
import com.tilicho.grocery.mangement.viewModel.AppViewModelStore
import com.tilicho.grocery.mangement.viewModel.AuthViewModel
import com.tilicho.grocery.mangement.viewModel.ListsViewModel

class ListsFragment : Fragment() {

    private lateinit var binding: FragmentListsBinding
    private val appViewModelStore by lazy { AppViewModelStore.getInstance(requireActivity().application) }
    private val listsViewModel by lazy { appViewModelStore.getAndroidViewModel<ListsViewModel>() }


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
        binding.listViewModel = listsViewModel
        initLitiners()
        addObservers()
        initTabLayout()

    }

    private fun addObservers() {
        listsViewModel.shouldShowEmptyUI.observe(viewLifecycleOwner) {

            if (!it) {

            } 
        }
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

    private fun initLitiners() {
        binding.createListButton.setOnClickListener { showCreateListDialog() }
        binding.listsTitle.setOnClickListener { listsViewModel.setUIState(!listsViewModel.shouldShowEmptyUI.value!!) }

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