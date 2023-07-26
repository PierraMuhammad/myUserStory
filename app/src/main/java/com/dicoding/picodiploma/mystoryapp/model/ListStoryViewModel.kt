package com.dicoding.picodiploma.mystoryapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.mystoryapp.data.response.ListStoryItem
import com.dicoding.picodiploma.mystoryapp.data.response.StoryRepository

class ListStoryViewModel(private val storyRepository: StoryRepository): ViewModel() {
    fun getStory(token: String) : LiveData<PagingData<ListStoryItem>> =
        storyRepository.getStories(token).cachedIn(viewModelScope)
}