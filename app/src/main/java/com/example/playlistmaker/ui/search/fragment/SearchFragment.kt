package com.example.playlistmaker.ui.search.fragment

import android.annotation.SuppressLint
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.playlistmaker.ui.player.activity.PlayerActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.search.view_model.SearchTracksState
import com.example.playlistmaker.ui.search.view_model.SearchTracksViewModel
import com.example.playlistmaker.util.BindingFragment
import com.example.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : BindingFragment<FragmentSearchBinding>() {

    private val viewModel: SearchTracksViewModel by viewModel()

    private var textWatcher: TextWatcher? = null

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private val adapter = TracksAdapter(
        object : TracksAdapter.OnItemClickListener {
            override fun onItemClick(track: Track) {
                onTrackClickDebounce(track)
            }
        }
    )

    override fun createBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchBinding {
        return FragmentSearchBinding.inflate(inflater, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY_MILLIS,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            viewModel.saveToHistory(track)
            PlayerActivity.show(requireContext(), track)
        }

        binding.recyclerView.adapter = adapter

        binding.btnClear.setOnClickListener {
            val inputMethodManager =
                requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)

            binding.searchEditText.setText("")
        }

        // кнопка Обновить при проблемах со связью
        binding.btnRenewPlaceholder.setOnClickListener {
            viewModel.searchDebounce(binding.searchEditText.text.toString())
        }

        binding.btnClearHistory.setOnClickListener {
            viewModel.clearHistory()
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val searchText = binding.searchEditText.text.toString()
                if (searchText.isNotEmpty()) {
                    viewModel.searchDebounce(searchText)
                }
                true
            }
            false
        }

        // обрабатываем изменение текста в строке поиска
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // если строка не пустая, показываем кнопку иначе прячем
                binding.btnClear.isVisible = !s.isNullOrEmpty()
                viewModel.searchDebounce(changedText = s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        textWatcher?.let { binding.searchEditText.addTextChangedListener(it) }

        // обрабатываем состояние фокуса строки поиска
        binding.searchEditText.setOnFocusChangeListener { view, hasFocus ->
            val searchText = binding.searchEditText.text.toString()
            if (hasFocus && searchText.isEmpty()) viewModel.searchDebounce(searchText)
        }

        viewModel.stateLiveData.observe(viewLifecycleOwner) {
            render(it)
        }
    }

    override fun onDestroyView() {
        textWatcher?.let { binding.searchEditText.removeTextChangedListener(it) }
        super.onDestroyView()
    }

    private fun render(state: SearchTracksState) {
        when (state) {
            is SearchTracksState.Loading -> showLoading()
            is SearchTracksState.NetworkContent -> showTracks(state.tracks)
            is SearchTracksState.HistoryContent -> showHistoryTracks(state.tracks)
            is SearchTracksState.Error -> showError()
            is SearchTracksState.Empty -> showEmpty()
        }
    }

    private fun showError() {
        binding.progressBar.isVisible = false
        binding.searchResult.isVisible = false

        binding.imagePlaceholder.setImageResource(R.drawable.no_internet_placeholder)
        binding.textPlaceholder.text = getString(R.string.no_internet)
        binding.btnRenewPlaceholder.isVisible = true

        binding.errorPlaceholder.isVisible = true
    }

    private fun showEmpty() {
        binding.progressBar.isVisible = false
        binding.searchResult.isVisible = false

        binding.imagePlaceholder.setImageResource(R.drawable.no_tracks_placeholder)
        binding.textPlaceholder.text = getString(R.string.nothing_found)
        binding.btnRenewPlaceholder.isVisible = false

        binding.errorPlaceholder.isVisible = true
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showTracks(tracks: List<Track>) {
        binding.errorPlaceholder.isVisible = false
        binding.progressBar.isVisible = false
        binding.historyHeader.isVisible = false
        binding.btnClearHistory.isVisible = false
        binding.searchResult.isVisible = true

        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showHistoryTracks(tracks: List<Track>) {
        binding.errorPlaceholder.isVisible = false
        binding.progressBar.isVisible = false

        if (tracks.isEmpty()) {
            binding.historyHeader.isVisible = false
            binding.btnClearHistory.isVisible = false
        } else {
            binding.historyHeader.isVisible = true
            binding.btnClearHistory.isVisible = true
        }

        binding.searchResult.isVisible = true

        adapter.tracks.clear()
        adapter.tracks.addAll(tracks)
        adapter.notifyDataSetChanged()
    }

    private fun showLoading() {
        binding.errorPlaceholder.isVisible = false
        binding.searchResult.isVisible = false
        binding.progressBar.isVisible = true
    }


    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }
}

