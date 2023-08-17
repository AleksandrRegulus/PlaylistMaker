package com.example.playlistmaker

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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
        val onHistoryTrackClickListener = object : TracksAdapter.OnItemClickListener {
            override fun onItemClick(track: Track) {
                searchHistory.addTrackToHistory(track, historyTracks)
                historyAdapter.notifyDataSetChanged()
            }
        }
        historyAdapter = TracksAdapter(historyTracks, onHistoryTrackClickListener)

        // завешиваем адаптер для основного списка поиска с листнером
        val onTrackClickListener = object : TracksAdapter.OnItemClickListener {
            override fun onItemClick(track: Track) {
                searchHistory.addTrackToHistory(track, historyTracks)
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
            showHistoryElements(View.GONE)
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
                binding.btnClear.visibility = clearButtonVisibility(s)

                // если пользователь очистил поисковый запрос очищаем результат поиска
                if (s?.isEmpty() == true) {
                    tracks.clear()
                    adapter.notifyDataSetChanged()
                }

                // если пользователь очистил поисковый показываем историю поиска
                val visibility = if (s?.isEmpty() == true) View.VISIBLE else View.GONE
                showHistoryElements(visibility)
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }
        binding.searchEditText.addTextChangedListener(simpleTextWatcher)

        // обрабатываем состояние фокуса строки поиска
        binding.searchEditText.setOnFocusChangeListener { view, hasFocus ->
            val visibility =
                if (hasFocus && binding.searchEditText.text.isEmpty()) View.VISIBLE else View.GONE
            showHistoryElements(visibility)
        }

    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
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
        if (text.isNotEmpty()) {
            tracks.clear()
            adapter.notifyDataSetChanged()
            binding.searchResult.visibility = View.GONE
            binding.errorPlaceholder.visibility = View.VISIBLE
            binding.textPlaceholder.text = text
            if (noInternet) {
                binding.imagePlaceholder.setImageResource(R.drawable.no_internet_placeholder)
                binding.btnRenewPlaceholder.visibility = View.VISIBLE
            } else {
                binding.imagePlaceholder.setImageResource(R.drawable.no_tracks_placeholder)
                binding.btnRenewPlaceholder.visibility = View.GONE
            }
        } else {
            binding.errorPlaceholder.visibility = View.GONE
            binding.searchResult.visibility = View.VISIBLE
        }
    }

    private fun search() {
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
    private fun showHistoryElements(visibility: Int) {
        if (visibility == View.VISIBLE && historyTracks.isNotEmpty()) {
            binding.historyHeader.visibility = View.VISIBLE
            binding.btnClearHistory.visibility = View.VISIBLE
            binding.recyclerView.adapter = historyAdapter
            historyAdapter.notifyDataSetChanged()
        } else {
            binding.historyHeader.visibility = View.GONE
            binding.btnClearHistory.visibility = View.GONE
            binding.recyclerView.adapter = adapter
        }


    }

    companion object {
        private const val SEARCH_TEXT = "SEARCH_TEXT"
        private const val ITUNES_BASE_URL = "https://itunes.apple.com"
    }
}

