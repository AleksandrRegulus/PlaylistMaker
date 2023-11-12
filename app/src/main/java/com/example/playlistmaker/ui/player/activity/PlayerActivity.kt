package com.example.playlistmaker.ui.player.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.view_model.PlayerState
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.io.Serializable

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding

    private var previewUrl: String = ""
    private val viewModel: PlayerViewModel by viewModel {
        parametersOf(previewUrl)
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
            binding.playingTime.text = getString(R.string.default_time)

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

                previewUrl = track.previewUrl

                viewModel.stateLiveData.observe(this) {
                    render(it)
                }

                binding.btnPlay.setOnClickListener {
                    viewModel.playbackControl()
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) {
            if (viewModel.stateLiveData.value is PlayerState.Playing) viewModel.playbackControl()
        }
    }

    private fun render(state: PlayerState) {
        when (state) {
            is PlayerState.Prepared -> showPrepared()
            is PlayerState.Paused -> showPause()
            is PlayerState.Playing -> showPlaying(state.currentPosition)
        }
    }

    private fun showPlaying(currentPosition: String) {
        binding.btnPlay.setImageResource(R.drawable.ic_pause)
        binding.playingTime.text = currentPosition
    }

    private fun showPrepared() {
        binding.btnPlay.isEnabled = true
        binding.btnPlay.setImageResource(R.drawable.ic_play)
    }

    private fun showPause() {
        binding.btnPlay.setImageResource(R.drawable.ic_play)
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
        private const val PUT_EXTRA_TAG = "track"

        fun show(context: Context, track: Track) {
            val intent = Intent(context, PlayerActivity::class.java)
            intent.putExtra(PUT_EXTRA_TAG, track)

            context.startActivity(intent)
        }
    }

}