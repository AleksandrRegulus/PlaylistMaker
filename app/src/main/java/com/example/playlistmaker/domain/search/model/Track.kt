package com.example.playlistmaker.domain.search.model

import java.io.Serializable

data class Track(
    val trackId: Int,
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val album: String, // альбом
    val releaseDate: String, //
    val trackTime: String, // Продолжительность трека
    val coverArtworkUrl100: String,   // ссылка на постер 100х100
    val coverArtworkUrl512: String,   // ссылка на постер 512х512
    val genre: String, //жанр
    val country: String,  // страна
    val previewUrl : String,  //ссылка на отрывок трека
) : Serializable

