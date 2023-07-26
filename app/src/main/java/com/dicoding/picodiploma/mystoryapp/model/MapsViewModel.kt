package com.dicoding.picodiploma.mystoryapp.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.mystoryapp.data.API.ApiConfig
import com.dicoding.picodiploma.mystoryapp.data.response.StoryResponse
import com.dicoding.picodiploma.mystoryapp.data.response.UserPreferenceDatastore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel(private val pref: UserPreferenceDatastore): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listStory = MutableLiveData<StoryResponse>()
    val listStory: LiveData<StoryResponse> = _listStory

    companion object{
        private const val tag = "MapsViewModel"
    }

    fun getListStoryLocation(token : String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().getStoriesWithLocation(bearer = "Bearer $token", 1)
        client.enqueue(object : Callback<StoryResponse>{
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                _isLoading.value = false
                if (response.isSuccessful){
                    _listStory.value = response.body()
                }
                else{
                    Log.e(tag, "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(tag, "onFailure: ${t.message}")
            }

        })
    }
}