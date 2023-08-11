package com.example.playlistmaker

import com.google.gson.annotations.*

data class Track(
    val trackId: Int,
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    @SerializedName("trackTimeMillis") val trackTime: String, // Продолжительность трека
    val artworkUrl100: String
) { // Ссылка на изображение обложки
}
