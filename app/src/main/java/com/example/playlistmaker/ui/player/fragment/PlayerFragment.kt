package com.example.playlistmaker.ui.player.fragment

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.main.view_model.HostViewModel
import com.example.playlistmaker.ui.media.playlist.fragment.PlaylistFragment
import com.example.playlistmaker.ui.player.view_model.BsPlaylistsState
import com.example.playlistmaker.ui.player.view_model.PlayerState
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.ui.search.fragment.SearchFragment
import com.example.playlistmaker.ui.search.fragment.TracksAdapter
import com.example.playlistmaker.util.BindingFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerFragment : BindingFragment<FragmentPlayerBinding>() {

    private val viewModel: PlayerViewModel by viewModel()
    private val hostViewModel by activityViewModel<HostViewModel>()
    private var currentTrack: Track? = null
    private var isClickAllowed = true

    private val adapter = BsPlaylistsAdapter(
        object : BsPlaylistsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                if (isClickAllowed) {
                    isClickAllowed = false
                    lifecycleScope.launch {
                        delay(CLICK_DEBOUNCE_DELAY_MILLIS)
                        isClickAllowed = true
                    }

                    if (currentTrack != null) {
                        viewModel.addTrackToPlaylist(position, currentTrack!!)
                    }
                }
            }
        }
    )


    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlayerBinding {
        return FragmentPlayerBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.playlistsRv.adapter = adapter

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBs).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    }

                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.btnAddToPlaylist.setOnClickListener {
            viewModel.getPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnNewPlaylist.setOnClickListener {
            if (isClickAllowed) {
                isClickAllowed = false
                lifecycleScope.launch {
                    delay(CLICK_DEBOUNCE_DELAY_MILLIS)
                    isClickAllowed = true
                }
                findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
            }
        }

        hostViewModel.getTrack().observe(viewLifecycleOwner) { track ->
            showTrackInfo(track)
        }

        hostViewModel.getWindowFocus().observe(viewLifecycleOwner) { hasFocus ->
            if (!hasFocus) {
                if (viewModel.stateLiveData.value is PlayerState.Playing) viewModel.playbackControl()
            }
        }

        viewModel.stateLiveData.observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.statePlaylistsLiveData.observe(viewLifecycleOwner) {
            renderBottomSheet(it)
            if (it is BsPlaylistsState.trackAdded) bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun showTrackInfo(track: Track) {
        currentTrack = track

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

    private fun renderBottomSheet(stateBs: BsPlaylistsState) {
        when (stateBs) {
            is BsPlaylistsState.playlistsContent -> {
                adapter.playlists.clear()
                adapter.playlists.addAll(stateBs.playlists)
                adapter.notifyDataSetChanged()
            }

            is BsPlaylistsState.trackAdded -> {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.track_added_to_playlist, stateBs.playlistName),
                    Toast.LENGTH_LONG
                ).show()
            }

            is BsPlaylistsState.trackExists -> {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.track_exists_in_playlist, stateBs.playlistName),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }

}