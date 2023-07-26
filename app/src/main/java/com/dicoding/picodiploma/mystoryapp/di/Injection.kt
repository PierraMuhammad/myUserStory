package com.dicoding.picodiploma.mystoryapp.di

import android.content.Context
import com.dicoding.picodiploma.mystoryapp.data.API.ApiConfig
import com.dicoding.picodiploma.mystoryapp.data.database.StoryDatabase
import com.dicoding.picodiploma.mystoryapp.data.response.StoryRepository

object Injection {
    fun provideRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val database = StoryDatabase.getDatabase(context)
        return StoryRepository(apiService, database)
    }
}