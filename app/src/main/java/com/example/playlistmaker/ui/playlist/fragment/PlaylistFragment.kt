package com.example.playlistmaker.ui.playlist.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.main.view_model.HostViewModel
import com.example.playlistmaker.ui.new_playlist.fragment.EditPlaylistFragment
import com.example.playlistmaker.ui.playlist.view_model.PlaylistViewModel
import com.example.playlistmaker.ui.search.fragment.TracksAdapter
import com.example.playlistmaker.util.BindingFragment
import com.example.playlistmaker.util.FileUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlaylistFragment : BindingFragment<FragmentPlaylistBinding>() {

    private val viewModel: PlaylistViewModel by viewModel()
    private val hostViewModel by activityViewModel<HostViewModel>()

    private var isClickAllowed = true

    private val adapter = PlaylistTracksAdapter(
        object : TracksAdapter.OnItemClickListener {
            override fun onItemClick(track: Track, position: Int) {
                if (isClickAllowed) {
                    isClickAllowed = false
                    lifecycleScope.launch {
                        delay(CLICK_DEBOUNCE_DELAY_MILLIS)
                        isClickAllowed = true
                    }
                    hostViewModel.setTrack(track)
                    findNavController().navigate(R.id.action_playlistFragment_to_playerFragment)
                }
            }
        },

        object : PlaylistTracksAdapter.OnItemLongClickListener {
            override fun onItemLongClick(trackId: Int, position: Int) {
                binding.overlay.isVisible = true
                val confirmDialog =
                    MaterialAlertDialogBuilder(requireContext(), R.style.Theme_MyApp_Dialog_Alert)
                        .setMessage(getString(R.string.delete_track_from_playlist_message))
                        .setNeutralButton(getString(R.string.confirm_playlist_cancel)) { dialog, which ->
                            binding.overlay.isVisible = false
                        }
                        .setPositiveButton(getString(R.string.delete_track_from_playlist_confirm)) { dialog, which ->
                            binding.overlay.isVisible = false
                            viewModel.deleteTrackFromPlaylist(trackId)
                        }
                confirmDialog.show()
            }
        }
    )

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistBinding {
        return FragmentPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menuBsBehavior = BottomSheetBehavior.from(binding.menuBs).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        menuBsBehavior.addBottomSheetCallback(object :
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

        binding.playlistRv.adapter = adapter

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.shareBtn.setOnClickListener {
            sharePlaylist()
        }

        binding.bsShareBtn.setOnClickListener {
            menuBsBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            sharePlaylist()
        }

        binding.bsEditPlaylistBtn.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistFragment_to_editPlaylistFragment,
                bundleOf(EditPlaylistFragment.ARGS_PLAYLIST_ID to viewModel.playlistLiveData.value?.playlist?.playlistId)
            )
        }

        binding.bsDeletePlaylistBtn.setOnClickListener {
            menuBsBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            binding.overlay2.isVisible = true
            val confirmDialog =
                MaterialAlertDialogBuilder(requireContext(), R.style.Theme_MyApp_Dialog_Alert)
                    .setMessage(getString(R.string.delete_playlist_message, viewModel.playlistLiveData.value?.playlist?.playlistName))
                    .setNegativeButton(getString(R.string.no)) { dialog, which ->
                        binding.overlay2.isVisible = false
                    }
                    .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                        FileUtil.deleteOldPosterFromStorage(
                            viewModel.playlistLiveData.value?.playlist?.posterUri ?: ""
                        )
                        viewModel.deletePlaylist()
                    }
            confirmDialog.show()
        }

        binding.menuBtn.setOnClickListener {
            menuBsBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        viewModel.playlistLiveData.observe(viewLifecycleOwner) { state ->
            with(binding) {

                setPoster(state.playlist.posterUri, posterIv)
                playlistNameTv.text = state.playlist.playlistName
                playlistDescriptionTv.text = state.playlist.playlistDescription
                numTracksTv.text = state.numTracks
                tracksDurationTv.text = state.tracksDuration

                bsPlaylistNameTv.text = state.playlist.playlistName
                bsNumberTracksTv.text = state.numTracks

                if (state.playlist.posterUri.isNotEmpty())
                    setPoster(state.playlist.posterUri, bsPosterIv)

                adapter.setTracks(state.tracks)

                binding.errorPlaceholder.isVisible = state.tracks.isEmpty()
            }
        }

        viewModel.playlistDeleted.observe(viewLifecycleOwner) {
            if (it) findNavController().navigateUp()
        }
    }

    private fun setPoster(posterUri: String, posterIv: ImageView) {
        if (posterUri == "") {
            posterIv.scaleType = ImageView.ScaleType.CENTER
            posterIv.setImageResource(R.drawable.track_placeholder_big)
        } else {
            posterIv.scaleType = ImageView.ScaleType.CENTER_CROP
            posterIv.setImageURI(posterUri.toUri())
        }
    }

    private fun sharePlaylist() {
        val playlistInfo = viewModel.playlistLiveData.value
        if (playlistInfo != null) {
            if (playlistInfo.playlist.numberOfTracks > 0) {
                val message = buildString {
                    append(playlistInfo.playlist.playlistName)

                    if (playlistInfo.playlist.playlistDescription.isNotEmpty())
                        append("\n${playlistInfo.playlist.playlistDescription}")

                    append("\n${playlistInfo.numTracks}")

                    var i = 1
                    playlistInfo.tracks.forEach {
                        append("\n${i++}. ${it.artistName} - ${it.trackName}")
                    }
                }

                Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, message)
                    type = "text/plain"
                    startActivity(Intent.createChooser(this, null))
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.playlist_is_empty),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onResume() {
        viewModel.getPlaylist(requireArguments().getInt(ARGS_PLAYLIST_ID))
        super.onResume()
    }

    companion object {
        const val ARGS_PLAYLIST_ID = "id"
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }

}