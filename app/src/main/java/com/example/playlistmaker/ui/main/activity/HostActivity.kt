package com.example.playlistmaker.ui.main.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityHostBinding
import com.example.playlistmaker.ui.App
import com.example.playlistmaker.ui.main.view_model.HostViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHostBinding

    private val viewModel: HostViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.container_view) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.playerFragment, R.id.newPlaylistFragment -> {
                    binding.divider.isVisible = false
                    binding.bottomNavigationView.isVisible = false
                }

                else -> {
                    binding.divider.isVisible = true
                    binding.bottomNavigationView.isVisible = true
                }
            }
        }

        viewModel.darkTheme.observe(this) { isDarkTheme ->
            (applicationContext as App).switchTheme(isDarkTheme)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        viewModel.setWindowFocus(hasFocus)
    }


}