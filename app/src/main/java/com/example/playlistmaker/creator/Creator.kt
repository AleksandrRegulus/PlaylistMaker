package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.network.TracksRepositoryImpl
import com.example.playlistmaker.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.data.repository.SharedPrefsRepositoryImpl
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.use_case.TracksInteractorImpl
import com.example.playlistmaker.domain.api.SharedPrefsRepository
import com.example.playlistmaker.domain.use_case.GetSearchHistoryUseCase
import com.example.playlistmaker.domain.use_case.GetThemeUseCase
import com.example.playlistmaker.domain.use_case.PlayerInteractorImpl
import com.example.playlistmaker.domain.use_case.SaveSearchHistoryUseCase
import com.example.playlistmaker.domain.use_case.SaveThemeUseCase

object Creator {

    fun provideGetThemeFromSharedPrefsUseCase(context: Context): GetThemeUseCase{
        return GetThemeUseCase(provideSharedPrefsRepository(context))
    }

    fun provideSaveThemeToSharedPrefsUseCase(context: Context): SaveThemeUseCase {
        return SaveThemeUseCase(provideSharedPrefsRepository(context))
    }

    fun provideGetSearchHistoryUseCase(context: Context): GetSearchHistoryUseCase{
        return GetSearchHistoryUseCase(provideSharedPrefsRepository(context))
    }

    fun provideSaveSearchHistoryUseCase(context: Context): SaveSearchHistoryUseCase{
        return SaveSearchHistoryUseCase(provideSharedPrefsRepository(context))
    }

    private fun provideSharedPrefsRepository(context: Context): SharedPrefsRepository {
        return SharedPrefsRepositoryImpl(context)
    }


    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }


    private fun getPalyerRepository(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(getPalyerRepository())
    }
}