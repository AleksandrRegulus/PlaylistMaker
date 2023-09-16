package com.example.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import com.example.playlistmaker.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var searchText = ""
    private lateinit var binding: ActivitySearchBinding

    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ItunesApi::class.java)
    private val tracks = ArrayList<Track>()
    private var historyTracks = ArrayList<Track>()

    private lateinit var adapter: TracksAdapter

    private lateinit var historyAdapter: TracksAdapter

    private val searchRunnable = Runnable { search() }
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

        // инициализируем класс для работы с SharedPreferences
        val searchHistory = SearchHistory(sharedPrefs)
        // подгружаем список "Вы искали"
        historyTracks = searchHistory.readHistory()

        // завешиваем адаптер для списка "Вы искали" с листнером (пока таким же как основной)
        val onHistoryTrackClickListener =
            TracksAdapter.OnItemClickListener { track ->
                if ( clickDebounce() ) {
                    searchHistory.addTrackToHistory(track, historyTracks)
                    historyAdapter.notifyDataSetChanged()

                    val displayIntent = Intent(this, PlayerActivity::class.java)
                    displayIntent.putExtra("track", track)
                    startActivity(displayIntent)
                }
            }
        historyAdapter = TracksAdapter(historyTracks, onHistoryTrackClickListener)

        // завешиваем адаптер для основного списка поиска с листнером
        val onTrackClickListener =
            TracksAdapter.OnItemClickListener { track ->
                if ( clickDebounce() ) {
                    searchHistory.addTrackToHistory(track, historyTracks)

                    val displayIntent = Intent(this, PlayerActivity::class.java)
                    displayIntent.putExtra(PUT_EXTRA_TAG, track)
                    startActivity(displayIntent)
                }
            }
        adapter = TracksAdapter(tracks, onTrackClickListener)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnClear.setOnClickListener {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)

            binding.errorPlaceholder.visibility = View.GONE
            binding.searchResult.visibility = View.VISIBLE
            binding.searchEditText.setText("")
        }

        // кнопка Обновить при проблемах со связью
        binding.btnRenewPlaceholder.setOnClickListener {
            search()
        }

        binding.btnClearHistory.setOnClickListener {
            historyTracks.clear()
            searchHistory.saveHistory(historyTracks)
            showHistoryElements(false)
        }

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.searchEditText.text.isNotEmpty()) {
                    search()
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
                binding.btnClear.isVisible = !s.isNullOrEmpty()

                // если пользователь очистил поисковый запрос очищаем результат поиска
                if (s?.isEmpty() == true) {
                    tracks.clear()
                    adapter.notifyDataSetChanged()
                    binding.progressBar.isVisible = false
                    handler.removeCallbacks(searchRunnable)  //отключаем автопоиск
                } else searchDebounce()  //запускаем автопоиск

                // если пользователь очистил поисковый показываем историю поиска
                showHistoryElements(s?.isEmpty() ?: true)
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        binding.searchEditText.addTextChangedListener(simpleTextWatcher)

        // обрабатываем состояние фокуса строки поиска
        binding.searchEditText.setOnFocusChangeListener { view, hasFocus ->
            showHistoryElements(hasFocus && binding.searchEditText.text.isEmpty())
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        searchText = savedInstanceState.getString(SEARCH_TEXT, "")
        binding.searchEditText.setText(searchText)
        if (searchText.isNotEmpty()) search()
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

    private fun search() {

        binding.errorPlaceholder.isVisible = false
        binding.searchResult.isVisible = false
        binding.progressBar.isVisible = true

        itunesService.findSongs(binding.searchEditText.text.toString())
            .enqueue(object : Callback<TracksResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<TracksResponse>,
                    response: Response<TracksResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.clear()
                                tracks.addAll(response.body()?.results!!)
                                adapter.notifyDataSetChanged()
                                showMessage("", false)

                            } else {
                                showMessage(getString(R.string.nothing_found), false)
                            }

                        }

                        else -> showMessage(getString(R.string.no_internet), true)
                    }
                }

                override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                    showMessage(getString(R.string.no_internet), true)
                }

            })
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showHistoryElements(visibility: Boolean) {
        binding.progressBar.isVisible = false
        if (visibility && historyTracks.isNotEmpty()) {
            binding.historyHeader.isVisible = true
            binding.btnClearHistory.isVisible = true
            binding.recyclerView.adapter = historyAdapter
            historyAdapter.notifyDataSetChanged()
        } else {
            binding.historyHeader.isVisible = false
            binding.btnClearHistory.isVisible = false
            binding.recyclerView.adapter = adapter
        }
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
        private const val PUT_EXTRA_TAG = "track"
        private const val ITUNES_BASE_URL = "https://itunes.apple.com"
        private const val CLICK_DEBOUNCE_DELAY_MILLIS = 1000L
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
    }
}

