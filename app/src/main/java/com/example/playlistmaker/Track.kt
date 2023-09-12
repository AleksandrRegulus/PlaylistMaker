package com.example.playlistmaker

import com.google.gson.annotations.*
import java.io.Serializable

data class Track(    // nullable отмечены только те поля, которые далее обрабатывем. Простое присвоение null вьюхам ошибки не вызывает
    val trackId: Int,
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    @SerializedName("collectionName") val album: String?, // альбом
    val releaseDate: String?, //
    val trackTimeMillis: String, // Продолжительность трека
    val artworkUrl100: String,   // ссылка на постер 100х100
    @SerializedName("primaryGenreName") val genre: String, //жанр
    val country: String,  // страна
    val previewUrl : String?,  //ссылка на отрывок трека
) : Serializable
