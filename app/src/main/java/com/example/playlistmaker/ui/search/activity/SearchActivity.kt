package com.example.playlistmaker.ui.search.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.ui.player.activity.PlayerActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.search.view_model.SearchTracksState
import com.example.playlistmaker.ui.search.view_model.SearchTracksViewModel

class SearchActivity : AppCompatActivity() {

    private var searchText = ""
    private var isClickAllowed = true

    private lateinit var binding: ActivitySearchBinding
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var viewModel: SearchTracksViewModel

    private var textWatcher: TextWatcher? = null

    private val adapter = TracksAdapter(
        object : TracksAdapter.OnItemClickListener {
            override fun onItemClick(track: Track) {
                if (clickDebounce()) {
                    viewModel.saveToHistory(track)
                    PlayerActivity.show(this@SearchActivity, track)
                }
            }
        }
    )

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)


        viewModel = ViewModelProvider(
            this,
            SearchTracksViewModel.getViewModelFactory()
        )[SearchTracksViewModel::class.java]

        binding.recyclerView.adapter = adapter

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnClear.setOnClickListener {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)

            binding.searchEditText.setText("")
        }

        // кнопка Обновить при проблемах со связью
        binding.btnRenewPlaceholder.setOnClickListener {
            if (searchText.isNotEmpty())
                viewModel.searchDebounce(searchText)
         }

        binding.btnClearHistory.setOnClickListener {
            viewModel.clearHistory()
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
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
                searchText = binding.searchEditText.text.toString()

                // если строка не пустая, показываем кнопку иначе прячем
                binding.btnClear.isVisible = !s.isNullOrEmpty()

                // если пользователь очистил поисковый запрос очищаем результат поиска
                if (s?.isEmpty() == true) {
                    viewModel.getHitsoryTracks()
                } else viewModel.searchDebounce(
                    changedText = s?.toString() ?: ""
                )  //запускаем автопоиск
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        textWatcher?.let { binding.searchEditText.addTextChangedListener(it) }

        // обрабатываем состояние фокуса строки поиска
        binding.searchEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && binding.searchEditText.text.isEmpty()) viewModel.getHitsoryTracks()
        }

        viewModel.stateLiveData.observe(this) {
            render(it)
        }
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


    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY_MILLIS)
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
    }
}

