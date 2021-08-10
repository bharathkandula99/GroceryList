package com.tilicho.grocery.mangement.fragments

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.FragmentListsBinding
import com.tilicho.grocery.mangement.databinding.FragmentSettingsBinding
import com.tilicho.grocery.mangement.dialog.AboutUsDialog
import com.tilicho.grocery.mangement.dialog.CreateListDialog
import com.tilicho.grocery.mangement.viewModel.AppViewModelStore

class ListsFragment : Fragment() {

    private lateinit var binding: FragmentListsBinding
    private val appViewModelStore by lazy { AppViewModelStore.getInstance(requireActivity().application) }

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
        initLitiners()

    }

    private fun initLitiners() {
        binding.createListButton.setOnClickListener { showCreateListDialog() }
    }


    private fun showCreateListDialog() {
        val dialog = CreateListDialog()
        dialog.show(requireActivity().supportFragmentManager, "create_list_dialog")
    }

}