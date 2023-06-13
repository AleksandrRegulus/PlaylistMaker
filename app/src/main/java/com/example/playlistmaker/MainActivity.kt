package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSearch = findViewById<Button>(R.id.btn_search)
        val btnMedia = findViewById<Button>(R.id.btn_media)
        val btnSettings = findViewById<Button>(R.id.btn_settings)

        btnSearch.setOnClickListener {
//            Toast.makeText(this@MainActivity, "Нажали на кнопку поиска!", Toast.LENGTH_SHORT).show()
            val displayIntent = Intent(this, SearchActivity::class.java)
            startActivity(displayIntent)
        }

        // TODO: вернуть лямбду
        val displayMediaIntent = Intent(this, MediaActivity::class.java)
        val btnMediaClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
//                Toast.makeText(this@MainActivity, "Нажмакали на кнопку медиа!", Toast.LENGTH_SHORT).show()
                startActivity(displayMediaIntent)
            }
        }
        btnMedia.setOnClickListener(btnMediaClickListener)

        btnSettings.setOnClickListener {
//            Toast.makeText(this@MainActivity, "Нажали на кнопку настроек!", Toast.LENGTH_SHORT).show()
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }
    }
}