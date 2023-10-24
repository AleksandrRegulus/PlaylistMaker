package com.example.playlistmaker.ui.settings.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.playlistmaker.ui.App
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.ui.settings.view_model.SettingsState
import com.example.playlistmaker.ui.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    private val viewModel: SettingsViewModel by viewModel()

    @SuppressLint("IntentReset")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnShareApp.setOnClickListener {
            Intent().apply {
                val message = getString(R.string.share_link)
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
                startActivity(Intent.createChooser(this, null))
            }
        }

        binding.btnSendToSupport.setOnClickListener {
            Intent().apply {
                val message = getString(R.string.email_message)
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_name)))
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
                startActivity(Intent.createChooser(this, null))
            }
        }


        binding.btnLicenseAgreement.setOnClickListener {
            Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(getString(R.string.offer_link))
                startActivity(this)
            }
        }

        binding.switchNightMode.setOnCheckedChangeListener { switcher, checked ->
            viewModel.saveTheme(checked)
        }

        viewModel.stateLiveData.observe(this) {
            render(it)
        }

        viewModel.getTheme()
    }

    private fun render(state: SettingsState) {
        val app = (applicationContext as App)

        when (state) {
            is SettingsState.DarkTheme -> app.darkTheme = true
            is SettingsState.LightTheme -> app.darkTheme = false
        }

        binding.switchNightMode.isChecked = app.darkTheme

        app.switchTheme(app.darkTheme)
    }

}