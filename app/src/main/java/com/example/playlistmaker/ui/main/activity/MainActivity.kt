package com.example.playlistmaker.ui.main.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.ui.App
import com.example.playlistmaker.ui.main.view_model.MainState
import com.example.playlistmaker.ui.main.view_model.MainViewModel
import com.example.playlistmaker.ui.media.activity.MediaActivity
import com.example.playlistmaker.ui.search.activity.SearchActivity
import com.example.playlistmaker.ui.settings.activity.SettingsActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSearch.setOnClickListener {
            val displayIntent = Intent(this, SearchActivity::class.java)
            startActivity(displayIntent)
        }

        binding.btnMedia.setOnClickListener {
            val displayIntent = Intent(this, MediaActivity::class.java)
            startActivity(displayIntent)
        }

        binding.btnSettings.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }

        viewModel.stateLiveData.observe(this) {
            render(it)
        }

        viewModel.getTheme()

    }

    private fun render(state: MainState) {
        val app = (applicationContext as App)
        app.switchTheme(state.darkTheme)
    }

}