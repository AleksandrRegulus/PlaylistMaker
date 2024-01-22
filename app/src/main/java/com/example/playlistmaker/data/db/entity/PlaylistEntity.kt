package com.example.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val playlistId: Int,
    val playlistName: String,
    val playlistDescription: String,
    val posterUri: String,
    val trackIDs: String,
    val numberOfTracks: Int,
)
