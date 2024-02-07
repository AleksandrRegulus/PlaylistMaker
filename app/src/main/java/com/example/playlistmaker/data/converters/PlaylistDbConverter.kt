package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.domain.playlist.model.Playlist
import com.google.gson.Gson

class PlaylistDbConverter() {

    fun map(playlist: Playlist, gson: Gson): PlaylistEntity {
        return with(playlist) {
            PlaylistEntity(
                playlistId = playlistId,
                playlistName = playlistName,
                playlistDescription = playlistDescription,
                posterUri = posterUri,
                trackIDs = gson.toJson(trackIDs),
                numberOfTracks = numberOfTracks
            )
        }
    }

    fun map(playListsEntity: PlaylistEntity, gson: Gson): Playlist {
        return with(playListsEntity) {
            Playlist(
                playlistId = playlistId,
                playlistName = playlistName,
                playlistDescription = playlistDescription,
                posterUri = posterUri,
                trackIDs = if (trackIDs.isNotEmpty())
                    gson.fromJson(trackIDs, Array<Int>::class.java).toList()
                else emptyList(),
                numberOfTracks = numberOfTracks
            )
        }
    }
}