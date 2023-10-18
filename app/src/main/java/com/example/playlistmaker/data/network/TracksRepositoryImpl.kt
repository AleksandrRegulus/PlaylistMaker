package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.TrackSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.data.mapper.TrackMapper
import com.example.playlistmaker.domain.model.Track
import com.example.playlistmaker.domain.api.TracksRepository

class TracksRepositoryImpl(private val networkClient: NetworkClient): TracksRepository {
    override fun searchTracks(expression: String): List<Track> {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        return if (response.resultCode == 200) {
            (response as TracksSearchResponse).results.map {
                TrackMapper.map(it)
            }
        } else {
            emptyList()
        }
    }
}