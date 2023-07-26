package com.dicoding.picodiploma.mystoryapp.data.response

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.dicoding.picodiploma.mystoryapp.data.API.ApiService
import com.dicoding.picodiploma.mystoryapp.data.StoryRemoteMediator
import com.dicoding.picodiploma.mystoryapp.data.database.StoryDatabase

class StoryRepository(private val apiService: ApiService, private val storyDatabase: StoryDatabase) {
    @OptIn(ExperimentalPagingApi::class)
    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        print("masuk ke getStories\n")
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, token),
            pagingSourceFactory = {
                storyDatabase.storyDao().getStories()
            }
        ).liveData
    }
}