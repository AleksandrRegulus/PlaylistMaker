package com.example.playlistmaker.ui.main.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityHostBinding
import com.example.playlistmaker.ui.App
import com.example.playlistmaker.ui.main.view_model.HostViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.koin.androidx.viewmodel.ext.android.viewModel

class HostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHostBinding

    private val viewModel: HostViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container_view) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(navController)

        viewModel.stateLiveData.observe(this) {isDarkTheme ->
            val app = (applicationContext as App)
            if (!app.isThemeSelected) {
                app.switchTheme(isDarkTheme)
                app.isThemeSelected = true
            }
        }
    }


}