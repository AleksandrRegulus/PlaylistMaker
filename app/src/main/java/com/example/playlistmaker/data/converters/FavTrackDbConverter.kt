package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.domain.search.model.Track

class FavTrackDbConverter {

    fun map(track: Track): TrackEntity {
        return TrackEntity(
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

    fun map(track: TrackEntity): Track{
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