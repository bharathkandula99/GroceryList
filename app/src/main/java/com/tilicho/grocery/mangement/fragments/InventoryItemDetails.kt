package com.tilicho.grocery.mangement.fragments

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.FragmentInventoryItemDetailsBinding
import com.tilicho.grocery.mangement.viewModel.AppViewModelStore
import com.tilicho.grocery.mangement.viewModel.InventoryViewModel


class InventoryItemDetails : Fragment() {

    private lateinit var binding: FragmentInventoryItemDetailsBinding
    private val appViewModelStore by lazy { AppViewModelStore.getInstance(requireActivity().application) }
    private val inventoryViewModel by lazy { appViewModelStore.getAndroidViewModel<InventoryViewModel>() }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_inventory_item_details, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setHasOptionsMenu(true);
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        //dialog = ProgressDialog(context)
        super.onActivityCreated(savedInstanceState)
        binding.inventoryViewModel = inventoryViewModel
        initLitiners()

    }

    private fun initLitiners() {

        binding.menu.setOnClickListener {

          //  setHasOptionsMenu(true)


        }

        binding.backArrow.setOnClickListener {
            binding.root.findNavController().navigateUp()
        }
    }

  /*  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overflow_menu,  menu )
        menu.add("Hi")
            .setIcon(android.R.drawable.btn_star)
            .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)

        super.onCreateOptionsMenu(menu, inflater)
    }*/


}