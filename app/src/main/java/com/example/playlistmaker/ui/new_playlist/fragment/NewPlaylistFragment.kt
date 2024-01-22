package com.example.playlistmaker.ui.new_playlist.fragment

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.ui.new_playlist.view_model.NewPlaylistState
import com.example.playlistmaker.ui.new_playlist.view_model.NewPlaylistViewModel
import com.example.playlistmaker.util.BindingFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class NewPlaylistFragment : BindingFragment<FragmentNewPlaylistBinding>() {

    private val viewModel: NewPlaylistViewModel by viewModel()

    private var textWatcher: TextWatcher? = null

    private var posterUri = ""
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNewPlaylistBinding {
        return FragmentNewPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            confirmDialogShow()
        }

        binding.backBtn.setOnClickListener {
            confirmDialogShow()
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                //обрабатываем событие выбора пользователем фотографии
                if (uri != null) {
                    binding.posterIv.setImageURI(uri)
                    binding.posterIv.scaleType = ImageView.ScaleType.CENTER_CROP
                    posterUri = uri.toString()
                }
            }

        binding.posterIv.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.createPlaylistBtn.isEnabled = false
                } else {
                    binding.createPlaylistBtn.isEnabled = true

                }
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        textWatcher?.let { binding.namePlaylistEt.addTextChangedListener(it) }

        binding.createPlaylistBtn.setOnClickListener {

            val savedFileUri =
                if (posterUri != "") saveImageToPrivateStorage(posterUri.toUri())
                else ""

            viewModel.createNewPlaylist(
                Playlist(
                    playlistId = 0,
                    playlistName = binding.namePlaylistEt.text.toString(),
                    playlistDescription = binding.descriptionPlaylistEt.text.toString(),
                    posterUri = savedFileUri,
                    trackIDs = emptyList(),
                    numberOfTracks = 0
                )
            )
        }

        viewModel.stateLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is NewPlaylistState.PlaylistCreated -> {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.create_playlist_done, state.playlistName),
                        Toast.LENGTH_LONG
                    ).show()
                    findNavController().navigateUp()
                }

                is NewPlaylistState.Error -> {
                    Toast.makeText(
                        requireContext(),
                        resources.getString(R.string.create_playlist_error, state.playlistName),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    }

    override fun onDestroyView() {
        textWatcher?.let { binding.namePlaylistEt.removeTextChangedListener(it) }
        super.onDestroyView()
    }

    private fun confirmDialogShow() {
        if (
            binding.namePlaylistEt.text.isNullOrEmpty() ||
            binding.descriptionPlaylistEt.text.isNotEmpty() ||
            posterUri != ""
        ) {
            val confirmDialog =
                MaterialAlertDialogBuilder(requireContext(), R.style.Theme_MyApp_Dialog_Alert)
                    .setTitle(getString(R.string.confirm_playlist_title))
                    .setMessage(getString(R.string.confirm_playlist_message))
                    .setNeutralButton(getString(R.string.confirm_playlist_cancel)) { dialog, which -> }
                    .setPositiveButton(getString(R.string.confirm_playlist_finish)) { dialog, which ->
                        findNavController().navigateUp()
                    }

            confirmDialog.show()
        } else findNavController().navigateUp()
    }

    private fun saveImageToPrivateStorage(uri: Uri): String {
        val filePath = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            PRIVATE_STORAGE_FOLDER_NAME
        )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "${System.currentTimeMillis()}.jpg")
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return file.toString()
    }

    companion object {
        private const val PRIVATE_STORAGE_FOLDER_NAME = "playlists_poster"
    }
}