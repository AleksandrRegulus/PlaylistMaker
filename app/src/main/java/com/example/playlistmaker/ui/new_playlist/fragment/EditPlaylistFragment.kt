package com.example.playlistmaker.ui.new_playlist.fragment

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.new_playlist.view_model.EditPlaylistViewModel
import com.example.playlistmaker.ui.playlist.fragment.PlaylistFragment
import com.example.playlistmaker.util.FileUtil
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditPlaylistFragment : NewPlaylistFragment() {
    override val viewModel: EditPlaylistViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createPlaylistBtn.text = resources.getString(R.string.save)
        binding.headerTv.text = resources.getString(R.string.edit)

        viewModel.getPlaylist(requireArguments().getInt(PlaylistFragment.ARGS_PLAYLIST_ID))

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.createPlaylistBtn.setOnClickListener {
            val savedFileUri: String
            val playlistPosterUri = viewModel.playlist.value?.posterUri ?: "empty"

            if (posterUri != "" && playlistPosterUri != "empty") {
                if (posterUri != playlistPosterUri) {
                    savedFileUri =
                        FileUtil.saveImageToPrivateStorage(requireActivity(), posterUri.toUri())
                    FileUtil.deleteOldPosterFromStorage(playlistPosterUri)
                } else savedFileUri = playlistPosterUri
            } else savedFileUri = ""

            viewModel.playlist.value?.let { playlist ->
                viewModel.updatePlaylist(
                    playlist.copy(
                        posterUri = savedFileUri,
                        playlistName = binding.namePlaylistEt.text.toString(),
                        playlistDescription = binding.descriptionPlaylistEt.text.toString()
                    )
                )
            }
        }

        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            with(binding) {
                viewModel.setPoster(playlist.posterUri)
                namePlaylistEt.setText(playlist.playlistName)
                descriptionPlaylistEt.setText(playlist.playlistDescription)
                createPlaylistBtn.isEnabled = true
            }
        }

        viewModel.playlistUpdated.observe(viewLifecycleOwner) { playlistUpdated ->
            if (playlistUpdated) findNavController().navigateUp()
        }
    }

    companion object {
        const val ARGS_PLAYLIST_ID = "id"
    }
}