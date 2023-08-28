package com.example.playlistmaker

import com.google.gson.annotations.*
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Locale

data class Track(    // nullable отмечены только те поля, которые далее обрабатывем. Простое присвоение null вьюхам ошибки не вызывает
    val trackId: Int,
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    @SerializedName("collectionName") val album: String?, // альбом
    val releaseDate: String?, //
    val trackTimeMillis: String, // Продолжительность трека
    val artworkUrl100: String?,   // ссылка на постер 100х100
    @SerializedName("primaryGenreName") val genre: String, //жанр
    val country: String  // страна
) : Serializable {

    fun getCoverArtwork() =
        if (artworkUrl100.isNullOrEmpty()) "" else artworkUrl100.replaceAfterLast( // получаем ссылку на большой постер
            '/',
            "512x512bb.jpg"
        )

    fun getYearFromReleaseDate() =
        if (releaseDate.isNullOrEmpty()) "" else releaseDate.substring(0..3)

    val timeFormat: String by lazy {   // что не так здесь? Получаю Null Pointer Exception
        getTimeFromMillis()
    }

    fun getTimeFromMillis(): String {
        val trackTimeInMilliseconds = trackTimeMillis.toLongOrNull()
        return if (trackTimeInMilliseconds != null) {
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTimeInMilliseconds)
        } else DEFAULT_TIME_STRING
    }

    companion object {
        private const val DEFAULT_TIME_STRING = "0:00"
    }
}
