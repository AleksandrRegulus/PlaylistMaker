package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.data.db.dao.FavTrackDao
import com.example.playlistmaker.data.db.dao.PlaylistsDao
import com.example.playlistmaker.data.db.dao.PlaylistsTrackDao
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.PlaylistsTrackEntity
import com.example.playlistmaker.data.db.entity.TrackEntity

@Database(
    version = 5, entities = [
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistsTrackEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favTrackDao(): FavTrackDao
    abstract fun playlistsDao(): PlaylistsDao
    abstract fun playlistsTrackDao(): PlaylistsTrackDao
}