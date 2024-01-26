package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.entity.PlaylistsTrackEntity
import com.example.playlistmaker.domain.search.model.Track

class PlaylistsTrackDbConverter {
    fun map(track: Track): PlaylistsTrackEntity {
        return PlaylistsTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            album = track.album,
            releaseDate = track.releaseDate,
            trackTime = track.trackTime,
            coverArtworkUrl100 = track.coverArtworkUrl100,
            coverArtworkUrl512 = track.coverArtworkUrl512,
            genre = track.genre,
            country = track.country,
            previewUrl = track.previewUrl,
            timeToAdd = System.currentTimeMillis()
        )
    }

    fun map(track: PlaylistsTrackEntity): Track {
        return Track(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            album = track.album,
            releaseDate = track.releaseDate,
            trackTime = track.trackTime,
            coverArtworkUrl100 = track.coverArtworkUrl100,
            coverArtworkUrl512 = track.coverArtworkUrl512,
            genre = track.genre,
            country = track.country,
            previewUrl = track.previewUrl,
            isFavorite = true
        )
    }
}