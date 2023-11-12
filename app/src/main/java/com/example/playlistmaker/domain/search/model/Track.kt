package com.example.playlistmaker.domain.search.model

import java.io.Serializable

data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val album: String,
    val releaseDate: String,
    val trackTime: String,
    val coverArtworkUrl100: String,
    val coverArtworkUrl512: String,
    val genre: String,
    val country: String,
    val previewUrl : String,
) : Serializable

