package com.example.playlistmaker.data.search.network

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.playlistmaker.data.search.dto.Response
import com.example.playlistmaker.data.search.dto.TrackSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val connectivityManager: ConnectivityManager,
    private val itunesService: ItunesApi
): NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = ERROR_CODE }
        }
        return if (dto is TrackSearchRequest) {
            withContext(Dispatchers.IO) {
                try {
                    val response = itunesService.searchTracks((dto).expression)
                    response.apply { resultCode = 200 }
                } catch (e: Throwable) {
                    Log.d("TAGERROR", e.message.orEmpty() )
                    Response().apply { resultCode = 500 }
                }
            }
        } else {
            Response().apply { resultCode = ERROR_CODE }
        }
    }

    private fun isConnected(): Boolean {
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }

    companion object {
        private const val ERROR_CODE = -1
    }
}