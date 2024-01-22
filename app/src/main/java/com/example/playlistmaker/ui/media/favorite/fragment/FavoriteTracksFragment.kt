package com.example.playlistmaker.ui.media.favorite.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.main.view_model.HostViewModel
import com.example.playlistmaker.ui.media.favorite.view_model.FavoriteState
import com.example.playlistmaker.ui.media.favorite.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.util.BindingFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment : BindingFragment<FragmentFavoriteTracksBinding>() {

    private val viewModel: FavoriteTracksViewModel by viewModel()
    private val hostViewModel by activityViewModel<HostViewModel>()

    private var isClickAllowed = true

    private val adapter = FavoriteAdapter(
        object : FavoriteAdapter.OnItemClickListener {
            override fun onItemClick(track: Track) {
                if (isClickAllowed) {
                    isClickAllowed = false
                    lifecycleScope.launch {
                        delay(CLICK_DEBOUNCE_DELAY_MILLIS)
                        isClickAllowed = true
                    }
                    hostViewModel.setTrack(track)
                    findNavController().navigate(R.id.action_mediaFragment_to_playerFragment)
                }
            }
        }
    )

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoriteTracksBinding {
        return FragmentFavoriteTracksBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = adapter

        viewModel.stateLiveData.observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavoriteTracks()
    }

    private fun render(state: FavoriteState) {
        when (state) {
            is FavoriteState.Empty -> showEmpty()
            is FavoriteState.FavoriteContent -> showContent(state.tracks)
        }
    }

    private fun showEmpty() {
        binding.errorPlaceholder.isVisible = true
        binding.favoriteTracks.isVisible = false
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showContent(tracks: List<Track>) {
        binding.errorPlaceholder.isVisible = false
        binding.favoriteTracks.isVisible = true

        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
        fun newInstance() = FavoriteTracksFragment()
    }
}