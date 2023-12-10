package com.example.playlistmaker.data.search.impl

import com.example.playlistmaker.data.search.network.NetworkClient
import com.example.playlistmaker.data.search.dto.TrackSearchRequest
import com.example.playlistmaker.data.search.dto.TracksSearchResponse
import com.example.playlistmaker.data.search.mapper.TrackMapper
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.data.search.TracksRepository
import com.example.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackSearchRequest(expression))
        when (response.resultCode) {
            ERROR_CODE -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }

            SUCCESS_CODE -> {
                emit(Resource.Success((response as TracksSearchResponse).results.map {
                    TrackMapper.map(it)
                }))
            }

            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }

    companion object {
        private const val ERROR_CODE = -1
        private const val SUCCESS_CODE = 200
    }
}