package com.example.playlistmaker.ui.player.activity

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.main.view_model.HostViewModel
import com.example.playlistmaker.ui.player.view_model.PlayerState
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.util.BindingFragment
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : BindingFragment<FragmentPlayerBinding>() {

    private val viewModel: PlayerViewModel by viewModel()
    private val hostViewModel by activityViewModel<HostViewModel>()
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlayerBinding {
        return FragmentPlayerBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        hostViewModel.getTrack().observe(viewLifecycleOwner) { track ->
            showTrackInfo(track)
        }

        hostViewModel.getWindowFocus().observe(viewLifecycleOwner) {hasFocus ->
            if (!hasFocus) {
                if (viewModel.stateLiveData.value is PlayerState.Playing) viewModel.playbackControl()
            }
        }

        viewModel.stateLiveData.observe(viewLifecycleOwner) {
            render(it)
        }
    }

    private fun showTrackInfo(track: Track) {
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

        Glide.with(this)
            .load(track.coverArtworkUrl512)
            .placeholder(R.drawable.track_placeholder_big)
            .fitCenter()
            .transform(
                RoundedCorners(
                    TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        resources.getFloat(R.dimen.poster_rounded_corner),
                        this.resources.displayMetrics
                    ).toInt()
                )
            )
            .into(binding.poster)

        binding.btnAddToFavorite.setOnClickListener {
            val changedTrack = track.copy(isFavorite = !track.isFavorite)
            viewModel.onFavoriteClicked(changedTrack)
            hostViewModel.setTrack(changedTrack)
        }

        if (track.isFavorite) binding.btnAddToFavorite.setImageResource(R.drawable.ic_in_favorite)
        else binding.btnAddToFavorite.setImageResource(R.drawable.ic_add_to_favorite)


        if (track.previewUrl.isNotEmpty()) {
            viewModel.preparePlayer(track.previewUrl)

            binding.btnPlay.setOnClickListener {
                viewModel.playbackControl()
            }
        }
    }

    private fun render(state: PlayerState) {
        when (state) {
            is PlayerState.Default -> showDefault()
            is PlayerState.Prepared -> showPrepared()
            is PlayerState.Paused -> showPause(state.currentPosition)
            is PlayerState.Playing -> showPlaying(state.currentPosition)
        }
    }

    private fun showDefault() {
        binding.btnPlay.isEnabled = false
        binding.btnPlay.setImageResource(R.drawable.ic_play)
        binding.playingTime.text = getString(R.string.default_time)
    }

    private fun showPrepared() {
        binding.btnPlay.isEnabled = true
        binding.btnPlay.setImageResource(R.drawable.ic_play)
        binding.playingTime.text = getString(R.string.default_time)
    }

    private fun showPause(currentPosition: String) {
        binding.btnPlay.setImageResource(R.drawable.ic_play)
        binding.playingTime.text = currentPosition
    }

    private fun showPlaying(currentPosition: String) {
        binding.btnPlay.setImageResource(R.drawable.ic_pause)
        binding.playingTime.text = currentPosition
    }


}