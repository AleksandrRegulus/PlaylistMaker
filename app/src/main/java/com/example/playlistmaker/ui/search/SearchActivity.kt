package com.example.playlistmaker.ui.search

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import com.example.playlistmaker.ui.player.PlayerActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.presentation.presenter.SearchTracksPresenter
import com.example.playlistmaker.presentation.presenter.SearchTracksView

class SearchActivity : AppCompatActivity(), SearchTracksView {

    private val getSearchHistoryUseCase = Creator.provideGetSearchHistoryUseCase(this)
    private val saveSearchHistoryUseCase = Creator.provideSaveSearchHistoryUseCase(this)
    private val presenter = SearchTracksPresenter(this)

    private var isHistory = false

    private var searchText = ""
    private lateinit var binding: ActivitySearchBinding

    private val tracks = ArrayList<Track>()
    private var historyTracks = ArrayList<Track>()

    private lateinit var adapter: TracksAdapter

    private val searchRunnable = Runnable { presenter.search(searchText) }
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // подгружаем список "Вы искали"
        historyTracks = getSearchHistoryUseCase.execute()

        // завешиваем адаптер для основного списка поиска с листнером
        val onTrackClickListener =
            TracksAdapter.OnItemClickListener { track ->
                if (clickDebounce()) {
                    val position = historyTracks.indexOf(track)
                    if (position == -1) {
                        if (historyTracks.size == MAX_TRACKS_IN_HISTORY) historyTracks.removeLast()
                    } else {
                        historyTracks.removeAt(position)
                    }
                    historyTracks.add(0, element = track)

                    saveSearchHistoryUseCase.execute(historyTracks)

                    PlayerActivity.show(this, track)
                }
            }
        adapter = TracksAdapter(tracks, onTrackClickListener)
        binding.recyclerView.adapter = adapter

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnClear.setOnClickListener {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)

            binding.searchEditText.setText("")
            showHistoryTracks()
        }

        // кнопка Обновить при проблемах со связью
        binding.btnRenewPlaceholder.setOnClickListener {
            if (searchText.isNotEmpty()) presenter.search(searchText)
        }

        binding.btnClearHistory.setOnClickListener {
            historyTracks.clear()
            saveSearchHistoryUseCase.execute(historyTracks)

            showTracks(ArrayList())
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (searchText.isNotEmpty()) {
                    presenter.search(searchText)
                }
                true
            }
            false
        }

        // обрабатываем изменение текста в строке поиска
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchText = binding.searchEditText.text.toString()

                // если строка не пустая, показываем кнопку иначе прячем
                binding.btnClear.isVisible = !s.isNullOrEmpty()

                // если пользователь очистил поисковый запрос очищаем результат поиска
                if (s?.isEmpty() == true) {
                    tracks.clear()
                    adapter.notifyDataSetChanged()
                    binding.progressBar.isVisible = false
                    handler.removeCallbacks(searchRunnable)  //отключаем автопоиск
                } else searchDebounce()  //запускаем автопоиск

            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        binding.searchEditText.addTextChangedListener(simpleTextWatcher)

        // обрабатываем состояние фокуса строки поиска
        binding.searchEditText.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus && binding.searchEditText.text.isEmpty()) showHistoryTracks()
        }
    }

    override fun onResume() {
        if (isHistory) showHistoryTracks()
        super.onResume()
    }
    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        searchText = savedInstanceState.getString(SEARCH_TEXT, "")
        binding.searchEditText.setText(searchText)
        if (searchText.isNotEmpty()) presenter.search(searchText)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showMessage(text: String, noInternet: Boolean) {
        binding.progressBar.isVisible = false
        if (text.isNotEmpty()) {
            tracks.clear()
            adapter.notifyDataSetChanged()
            binding.searchResult.isVisible = false
            binding.errorPlaceholder.isVisible = true
            binding.textPlaceholder.text = text
            if (noInternet) {
                binding.imagePlaceholder.setImageResource(R.drawable.no_internet_placeholder)
                binding.btnRenewPlaceholder.isVisible = true
            } else {
                binding.imagePlaceholder.setImageResource(R.drawable.no_tracks_placeholder)
                binding.btnRenewPlaceholder.isVisible = false
            }
        } else {
            binding.errorPlaceholder.isVisible = false
            binding.searchResult.isVisible = true
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun showTracks(arrayTracks: ArrayList<Track>) {
        binding.errorPlaceholder.isVisible = false
        binding.progressBar.isVisible = false
        binding.historyHeader.isVisible = false
        binding.btnClearHistory.isVisible = false
        binding.searchResult.isVisible = true
        tracks.clear()
        tracks.addAll(arrayTracks)
        adapter.notifyDataSetChanged()
        isHistory = false
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showHistoryTracks() {
        if (historyTracks.isNotEmpty()) {
            binding.errorPlaceholder.isVisible = false
            binding.progressBar.isVisible = false
            binding.historyHeader.isVisible = true
            binding.btnClearHistory.isVisible = true
            binding.searchResult.isVisible = true
            tracks.clear()
            tracks.addAll(historyTracks)
            adapter.notifyDataSetChanged()
            isHistory = true
        }
    }

    override fun showProgressBar() {
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

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY_MILLIS)
    }

    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
        private const val MAX_TRACKS_IN_HISTORY = 10
    }
}

