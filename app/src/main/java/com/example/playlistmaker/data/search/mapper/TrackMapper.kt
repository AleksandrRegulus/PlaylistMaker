package com.example.playlistmaker.data.search.mapper

import com.example.playlistmaker.data.search.dto.TrackDto
import com.example.playlistmaker.domain.search.model.Track

object TrackMapper {

    fun map(track: TrackDto, isFavorite: Boolean): Track {
        return with(track) {
            Track(
                trackId = trackId,
                trackName = trackName,
                artistName = artistName,
                album = album ?: "",
                releaseDate = FormatTrack.getYearFromReleaseDate(releaseDate),
                trackTime = FormatTrack.getTimeFromMillis(trackTimeMillis),
                trackTimeMillis = trackTimeMillis.toLongOrNull() ?: 0,
                coverArtworkUrl100 = artworkUrl100 ?: "",
                coverArtworkUrl512 = FormatTrack.getCoverArtwork(artworkUrl100),
                genre = genre ?: "",
                country = country ?: "",
                previewUrl = previewUrl ?: "",
                isFavorite = isFavorite
            )
        }
    }
}