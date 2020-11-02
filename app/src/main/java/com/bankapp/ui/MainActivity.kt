package com.bankapp.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.bankapp.R
import com.bankapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val viewModel: SharedViewModel by viewModels() /** Uses a delegate function to create the viewModel */
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)

        /** This block initializes the toolbar's title and removes the back arrow button
            since MainActivity starts by showing the DisplayAccountsFragment which does not need a
            back button on it's toolbar */
        with(supportActionBar){
            this?.title = getString(R.string.app_name)
            this?.setDisplayHomeAsUpEnabled(false)
            this?.setDisplayShowHomeEnabled(false)
        }
        /** Initializes the Navigation component*/
        val navHostFragment = supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        navController = navHostFragment.navController
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /** This is for the behavior of the back arrow button on the toolbar, it simply acts as on onBackPressed() */
        return when(item.itemId){
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
