package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.PlaylistsTrackEntity

@Dao
interface PlaylistsTrackDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistsTrack(playlistsTrackEntity: PlaylistsTrackEntity): Long

    @Query("SELECT * FROM playlists_tracks_table WHERE trackId IN (:trackIdList) ORDER BY timeToAdd DESC")
    suspend fun getPlaylistTracks(trackIdList: List<Int>): List<PlaylistsTrackEntity>

    @Query("DELETE FROM playlists_tracks_table WHERE trackId = :trackId")
    suspend fun deleteTrack(trackId: Int)
}