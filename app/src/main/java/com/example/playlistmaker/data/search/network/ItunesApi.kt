package com.example.playlistmaker.data.search.network

import com.example.playlistmaker.data.search.dto.TracksSearchResponse
import retrofit2.http.*

interface ItunesApi {

    @GET("/search?entity=song&")
    suspend fun searchTracks(@Query("term") text: String): TracksSearchResponse
}
