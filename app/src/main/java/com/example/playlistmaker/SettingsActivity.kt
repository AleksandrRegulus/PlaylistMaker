package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

class SettingsActivity : AppCompatActivity() {
    @SuppressLint("IntentReset")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnBack = findViewById<ImageView>(R.id.btn_back)
        val btnShareApp = findViewById<Button>(R.id.btn_share_app)
        val btnSendToSupport = findViewById<Button>(R.id.btn_send_to_support)
        val btnLicenseAgreement = findViewById<Button>(R.id.btn_license_agreement)

        btnBack.setOnClickListener {
            finish()
        }

        btnShareApp.setOnClickListener {
            val intent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(intent, null)
            startActivity(shareIntent)
        }

        btnSendToSupport.setOnClickListener {
            val message = getString(R.string.email_message)
            val intent: Intent = Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_name)))
                putExtra(Intent.EXTRA_TEXT, message)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(intent, null)
            startActivity(shareIntent)
        }


        btnLicenseAgreement.setOnClickListener {
            val url = Uri.parse(getString(R.string.offer_link))
            val intent = Intent(Intent.ACTION_VIEW, url)
            startActivity(intent)
        }

    }
}