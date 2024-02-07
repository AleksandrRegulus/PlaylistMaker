package com.example.playlistmaker.data.converters

import com.example.playlistmaker.data.db.entity.PlaylistsTrackEntity
import com.example.playlistmaker.domain.search.model.Track

class PlaylistsTrackDbConverter {
    fun map(track: Track): PlaylistsTrackEntity {
        return with(track) {
            PlaylistsTrackEntity(
                trackId = trackId,
                trackName = trackName,
                artistName = artistName,
                album = album,
                releaseDate = releaseDate,
                trackTime = trackTime,
                trackTimeMillis = trackTimeMillis,
                coverArtworkUrl100 = coverArtworkUrl100,
                coverArtworkUrl512 = coverArtworkUrl512,
                genre = genre,
                country = country,
                previewUrl = previewUrl,
                timeToAdd = System.currentTimeMillis()
            )
        }
    }

    fun map(track: PlaylistsTrackEntity, isFavorite: Boolean): Track {
        return with(track) {
            Track(
                trackId = trackId,
                trackName = trackName,
                artistName = artistName,
                album = album,
                releaseDate = releaseDate,
                trackTime = trackTime,
                trackTimeMillis = trackTimeMillis,
                coverArtworkUrl100 = coverArtworkUrl100,
                coverArtworkUrl512 = coverArtworkUrl512,
                genre = genre,
                country = country,
                previewUrl = previewUrl,
                isFavorite = isFavorite
            )
        }
    }
}