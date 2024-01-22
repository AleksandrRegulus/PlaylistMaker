package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TrackEntity

@Dao
interface FavTrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackToFav(track: TrackEntity)

    @Query("DELETE FROM favorite_tracks_table WHERE trackId = :trackId")
    suspend fun deleteTrackFromFav(trackId: Int)

    @Query("SELECT * FROM favorite_tracks_table ORDER BY timeToAddToFav DESC")
    suspend fun getFavTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM favorite_tracks_table")
    suspend fun getFavTracksIDs(): List<Int>
}