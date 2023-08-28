package com.example.playlistmaker

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import java.io.Serializable

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }


        val track = intent.serializable<Track>("track")

        if (track != null) {
            binding.artistName.text = track.artistName
            binding.trackName.text = track.trackName
            binding.trackTime.text = FormatTrack.getTimeFromMillis(track.trackTimeMillis)
            binding.playingTime.text = FormatTrack.DEFAULT_TIME_STRING

            if (track.album.isNullOrEmpty()) {
                binding.albumName.visibility = View.GONE
                binding.album.visibility = View.GONE
            } else {
                binding.albumName.visibility = View.VISIBLE
                binding.album.visibility = View.VISIBLE
                binding.albumName.text = track.album
            }

            if (track.releaseDate.isNullOrEmpty()) {
                binding.yearName.visibility = View.GONE
                binding.year.visibility = View.GONE
            } else {
                binding.yearName.visibility = View.VISIBLE
                binding.year.visibility = View.VISIBLE
                binding.yearName.text = FormatTrack.getYearFromReleaseDate(track.releaseDate)
            }

            binding.genreName.text = track.genre
            binding.countryName.text = track.country

            val roundedCorner = 8f  // величина скругления углов картинки в dp

            Glide.with(this)
                .load(FormatTrack.getCoverArtwork(track.artworkUrl100))
                .placeholder(R.drawable.track_placeholder_big)
                .fitCenter()
                .transform(
                    RoundedCorners(
                        TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            roundedCorner,
                            this.resources.displayMetrics
                        ).toInt()
                    )
                )
                .into(binding.poster)

        }

    }

    // получаем объект из другой активити используя нужную функцию десериализации в зависимости от версии ОС
    private inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(
            key,
            T::class.java
        )

        else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
    }

}