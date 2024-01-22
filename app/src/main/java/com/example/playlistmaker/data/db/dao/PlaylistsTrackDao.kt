package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.playlistmaker.data.db.entity.PlaylistsTrackEntity

@Dao
interface PlaylistsTrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistsTrack(playlistsTrackEntity: PlaylistsTrackEntity): Long
}