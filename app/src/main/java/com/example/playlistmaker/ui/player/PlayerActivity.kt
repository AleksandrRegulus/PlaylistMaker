package com.example.playlistmaker.ui.player

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.model.Track
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private val playerInteractor = Creator.providePlayerInteractor()

    private lateinit var binding: ActivityPlayerBinding

    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())

    private val timerRunnable = object : Runnable {
        override fun run() {
            binding.playingTime.text =
                SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(playerInteractor.getCurrentPosition())
            handler.postDelayed(this, DELAY_MILLIS)
        }
    }

    private val getStateRunnable = object : Runnable {
        override fun run() {
            playerState = playerInteractor.getState()
            when (playerState) {
                STATE_PLAYING, STATE_DEFAULT -> {
                    handler.postDelayed(this, DELAY_STATE_MILLIS)
                }
                STATE_PREPARED -> {
                    binding.btnPlay.isEnabled = true
                    binding.btnPlay.setImageResource(R.drawable.ic_play)
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }


        val track = intent.serializable<Track>(PUT_EXTRA_TAG)

        if (track != null) {
            binding.artistName.text = track.artistName
            binding.trackName.text = track.trackName
            binding.trackTime.text = track.trackTime
            binding.playingTime.text = DEFAULT_TIME_STRING

            if (track.album.isEmpty()) {
                binding.albumName.isVisible = false
                binding.album.isVisible = false
            } else {
                binding.albumName.isVisible = true
                binding.album.isVisible = true
                binding.albumName.text = track.album
            }

            if (track.releaseDate.isEmpty()) {
                binding.yearName.isVisible = false
                binding.year.isVisible = false
            } else {
                binding.yearName.isVisible = true
                binding.year.isVisible = true
                binding.yearName.text = track.releaseDate
            }

            binding.genreName.text = track.genre
            binding.countryName.text = track.country

            val roundedCorner = 8f  // величина скругления углов картинки в dp

            Glide.with(this)
                .load(track.coverArtworkUrl512)
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

            if (track.previewUrl.isNotEmpty()) {
                playerInteractor.preparePlayer(track.previewUrl)
                handler.post(getStateRunnable)

                binding.btnPlay.setOnClickListener {
                    playerInteractor.playbackControl()
                    playerState = playerInteractor.getState()
                    when (playerState) {
                        STATE_PLAYING -> {
                            binding.btnPlay.setImageResource(R.drawable.ic_pause)
                            handler.post(timerRunnable)
                            handler.post(getStateRunnable)
                        }
                        STATE_PAUSED -> {
                            pause()
                        }
                    }
                }
            }
        }

    }

    private fun pause() {
        binding.btnPlay.setImageResource(R.drawable.ic_play)
        handler.removeCallbacks(getStateRunnable)
        handler.removeCallbacks(timerRunnable)
    }

    override fun onPause() {
        super.onPause()
        playerInteractor.pausePlayer()
        pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        pause()
        playerInteractor.release()
    }


    // получаем объект из другой активити используя нужную функцию десериализации в зависимости от версии ОС
    private inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(
            key,
            T::class.java
        )

        else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY_MILLIS = 300L
        private const val DELAY_STATE_MILLIS = 200L
        private const val PUT_EXTRA_TAG = "track"
        private const val DEFAULT_TIME_STRING = "00:00"

        fun show(context: Context, track: Track) {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra(PUT_EXTRA_TAG, track)

            context.startActivity(intent)
        }
    }

}