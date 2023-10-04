package com.example.playlistmaker.data.mapper

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.model.Track

object TrackMapper {

    fun map(track: TrackDto): Track {
        return Track(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            album = track.album ?: "",
            releaseDate = FormatTrack.getYearFromReleaseDate(track.releaseDate),
            trackTime = FormatTrack.getTimeFromMillis(track.trackTimeMillis),
            coverArtworkUrl100 = track.artworkUrl100 ?: "",
            coverArtworkUrl512 = FormatTrack.getCoverArtwork(track.artworkUrl100),
            genre = track.genre ?: "",
            country = track.country ?: "",
            previewUrl = track.previewUrl ?: "",
        )
    }
}