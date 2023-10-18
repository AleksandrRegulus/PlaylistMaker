package com.example.playlistmaker.ui.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.playlistmaker.ui.App
import com.example.playlistmaker.R
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private val saveThemeUseCase = Creator.provideSaveThemeToSharedPrefsUseCase(this)
    private lateinit var binding: ActivitySettingsBinding

    @SuppressLint("IntentReset")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val app = (applicationContext as App)
        if (app.darkTheme) binding.switchNightMode.isChecked = true

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
            app.darkTheme = checked
            saveThemeUseCase.execute(checked)
            app.switchTheme(checked)
        }

    }

}