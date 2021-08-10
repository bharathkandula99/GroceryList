package com.tilicho.grocery.mangement.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tilicho.grocery.mangement.R
import com.tilicho.grocery.mangement.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView<ActivityHomeBinding>(
            this,
            R.layout.activity_home
        )
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.homeActivityFragmentContainer) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            NavigationUI.onNavDestinationSelected(it, navController)
        }
        binding.bottomNavigation.setOnNavigationItemReselectedListener { item ->
            when (item.itemId) {
                R.id.fragment_list -> {
                    toast("reselected page 1")
                    // Respond to navigation item 1 click

                }
                R.id.fragment_inventory -> {
                    toast("reselected page 2")
                    // Respond to navigation item 2 click

                }
                R.id.fragment_summery -> {
                    toast("reselected page 3")
                    // Respond to navigation item 1 click

                }
                R.id.fragment_settings -> {
                    toast("reselected page 4")
                    // Respond to navigation item 2 click

                }

            }
        }

        binding.bottomNavigation.selectedItemId = R.id.fragment_list


    }
    fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}