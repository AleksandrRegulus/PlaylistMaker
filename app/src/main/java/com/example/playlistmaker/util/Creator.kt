package com.example.playlistmaker.util

import android.content.Context
import com.example.playlistmaker.data.search.network.RetrofitNetworkClient
import com.example.playlistmaker.data.search.impl.TracksRepositoryImpl
import com.example.playlistmaker.data.SharedPrefsRepositoryImpl
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.data.search.TracksRepository
import com.example.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.api.SharedPrefsRepository
import com.example.playlistmaker.domain.search.impl.GetSearchHistoryUseCase
import com.example.playlistmaker.domain.settings.impl.GetThemeUseCase
import com.example.playlistmaker.domain.search.impl.SaveSearchHistoryUseCase
import com.example.playlistmaker.domain.settings.impl.SaveThemeUseCase

object Creator {

    fun provideGetThemeFromSharedPrefsUseCase(context: Context): GetThemeUseCase {
        return GetThemeUseCase(provideSharedPrefsRepository(context))
    }

    fun provideSaveThemeToSharedPrefsUseCase(context: Context): SaveThemeUseCase {
        return SaveThemeUseCase(provideSharedPrefsRepository(context))
    }

    fun provideGetSearchHistoryUseCase(context: Context): GetSearchHistoryUseCase {
        return GetSearchHistoryUseCase(provideSharedPrefsRepository(context))
    }

    fun provideSaveSearchHistoryUseCase(context: Context): SaveSearchHistoryUseCase {
        return SaveSearchHistoryUseCase(provideSharedPrefsRepository(context))
    }

    private fun provideSharedPrefsRepository(context: Context): SharedPrefsRepository {
        return SharedPrefsRepositoryImpl(context)
    }


    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

}