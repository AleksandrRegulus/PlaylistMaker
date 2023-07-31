package com.example.playlistmaker

import retrofit2.Call
import retrofit2.http.*

interface ItunesApi {

    @GET("/search?entity=song&")
    fun findSongs(@Query("term") text: String): Call<TracksResponse>
}
