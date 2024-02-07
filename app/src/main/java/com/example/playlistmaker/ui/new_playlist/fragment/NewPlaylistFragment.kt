package com.example.playlistmaker.ui.new_playlist.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.example.playlistmaker.ui.App
import com.example.playlistmaker.ui.new_playlist.view_model.NewPlaylistState
import com.example.playlistmaker.ui.new_playlist.view_model.NewPlaylistViewModel
import com.example.playlistmaker.util.BindingFragment
import com.example.playlistmaker.util.FileUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

open class NewPlaylistFragment : BindingFragment<FragmentNewPlaylistBinding>() {

    open val viewModel: NewPlaylistViewModel by viewModel()

    private var textWatcherPlaylistName: TextWatcher? = null
    private var textWatcherPlaylistDescription: TextWatcher? = null

    open var posterUri = ""
    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentNewPlaylistBinding {
        return FragmentNewPlaylistBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            confirmDialogShow()
        }

        binding.backBtn.setOnClickListener {
            confirmDialogShow()
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    viewModel.setPoster(uri.toString())
                }
            }

        binding.posterIv.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        textWatcherPlaylistName = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setTextInputLayoutStyle(s, binding.namePlaylistTil)
                binding.createPlaylistBtn.isEnabled = !s?.trim().isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        textWatcherPlaylistName?.let { binding.namePlaylistEt.addTextChangedListener(it) }

        textWatcherPlaylistDescription = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                setTextInputLayoutStyle(s, binding.descriptionPlaylistTil)
                binding.createPlaylistBtn.isEnabled = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        textWatcherPlaylistDescription?.let {
            binding.descriptionPlaylistEt.addTextChangedListener(it)
        }

        binding.createPlaylistBtn.setOnClickListener {

            val savedFileUri =
                if (posterUri != "") FileUtil.saveImageToPrivateStorage(
                    requireActivity(),
                    posterUri.toUri()
                )
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

        viewModel.posterLiveData.observe(viewLifecycleOwner) { uri ->
            posterUri = uri
            if (uri.isNotEmpty()) {
                binding.posterIv.setImageURI(uri.toUri())
                binding.posterIv.scaleType = ImageView.ScaleType.CENTER_CROP
            } else {
                binding.posterIv.setImageResource(R.drawable.ic_add_photo)
                binding.posterIv.scaleType = ImageView.ScaleType.CENTER
            }
        }
    }

    override fun onDestroyView() {
        textWatcherPlaylistName?.let { binding.namePlaylistEt.removeTextChangedListener(it) }
        textWatcherPlaylistDescription?.let {
            binding.descriptionPlaylistEt.removeTextChangedListener(
                it
            )
        }
        super.onDestroyView()
    }

    private fun setTextInputLayoutStyle(s: CharSequence?, textInputLayout: TextInputLayout) {
        val colorTilStroke =
            if ((requireActivity().applicationContext as App).darkTheme)
                R.color.selector_color_til_no_text_dark_theme
            else R.color.selector_color_til_no_text_light_theme

        val hintTextColor =
            if ((requireActivity().applicationContext as App).darkTheme)
                R.color.selector_color_til_no_text_dark_theme
            else R.color.selector_color_til_hint_no_text_light_theme

        if (s.isNullOrEmpty()) {
            textInputLayout.setBoxStrokeColorStateList(
                AppCompatResources.getColorStateList(requireActivity(), colorTilStroke)
            )

            textInputLayout.defaultHintTextColor =
                AppCompatResources.getColorStateList(requireActivity(), hintTextColor)
        } else {
            textInputLayout.setBoxStrokeColorStateList(
                AppCompatResources.getColorStateList(
                    requireActivity(),
                    R.color.selector_color_til_text
                )
            )

            textInputLayout.defaultHintTextColor =
                AppCompatResources.getColorStateList(
                    requireActivity(),
                    R.color.selector_color_til_text
                )
        }
    }

    private fun confirmDialogShow() {
        if (
            !binding.namePlaylistEt.text.isNullOrEmpty() ||
            !binding.descriptionPlaylistEt.text.isNullOrEmpty() ||
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

}