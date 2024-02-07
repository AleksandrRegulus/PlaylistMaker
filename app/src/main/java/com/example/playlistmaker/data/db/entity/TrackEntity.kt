package com.example.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tracks_table")
data class TrackEntity(
    @PrimaryKey
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val album: String,
    val releaseDate: String,
    val trackTime: String,
    val trackTimeMillis: Long,
    val coverArtworkUrl100: String,
    val coverArtworkUrl512: String,
    val genre: String,
    val country: String,
    val previewUrl : String,
    val timeToAdd: Long,
)
