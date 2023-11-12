package com.example.playlistmaker.di

import android.content.Context
import android.net.ConnectivityManager
import com.example.playlistmaker.data.search.network.ItunesApi
import com.example.playlistmaker.data.search.network.NetworkClient
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import org.koin.android.ext.koin.androidContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import org.koin.dsl.module

val dataModule = module {

    single<ItunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(
            androidContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager,
            get()
        )
    }
}