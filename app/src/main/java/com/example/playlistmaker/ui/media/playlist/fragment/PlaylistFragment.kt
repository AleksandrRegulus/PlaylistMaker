package com.example.playlistmaker.ui.media.playlist.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.ui.media.playlist.view_model.PlaylistViewModel
import com.example.playlistmaker.ui.media.playlist.view_model.PlaylistsState
import com.example.playlistmaker.util.BindingFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : BindingFragment<FragmentPlaylistBinding>() {

    private val viewModel: PlaylistViewModel by viewModel()

    private var isClickAllowed = true
    private val adapter = PlaylistAdapter()

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPlaylistBinding {
        return FragmentPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNewPlaylist.setOnClickListener {
            if (isClickAllowed) {
                isClickAllowed = false
                lifecycleScope.launch {
                    delay(CLICK_DEBOUNCE_DELAY_MILLIS)
                    isClickAllowed = true
                }
                findNavController().navigate(R.id.action_mediaFragment_to_newPlaylistFragment)
            }
        }

        binding.playlistsRv.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistsRv.adapter = adapter

        viewModel.stateLiveData.observe(viewLifecycleOwner) {
            render(it)
        }

    }

    override fun onResume() {
        viewModel.getPlaylists()
        super.onResume()
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Empty -> showPlaceholder()
            is PlaylistsState.PlaylistContent -> showContent(state.playlists)
        }
    }

    private fun showPlaceholder() {
        binding.playlistsRv.isVisible = false
        binding.errorPlaceholder.isVisible = true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showContent(playlists: List<Playlist>) {
        binding.playlistsRv.isVisible = true
        binding.errorPlaceholder.isVisible = false

        adapter.playlists.clear()
        adapter.playlists.addAll(playlists)
        adapter.notifyDataSetChanged()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L

        fun newInstance() = PlaylistFragment()
    }

}