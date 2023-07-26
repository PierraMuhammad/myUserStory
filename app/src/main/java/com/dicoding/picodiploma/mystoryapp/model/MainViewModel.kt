package com.dicoding.picodiploma.mystoryapp.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.mystoryapp.data.API.ApiConfig
import com.dicoding.picodiploma.mystoryapp.data.response.AddNewStoryResponse
import com.dicoding.picodiploma.mystoryapp.data.response.ListStoryItem
import com.dicoding.picodiploma.mystoryapp.data.response.StoryResponse
import com.dicoding.picodiploma.mystoryapp.data.response.UserPreferenceDatastore
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MainViewModel(private val pref: UserPreferenceDatastore): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> = _listStory

    companion object{
        private const val tag = "MainViewModel"
    }

/*
    fun getListStory(token: String){
        _isLoading.value = true
        val client = ApiConfig.getApiService().getListStory(bearer = "Bearer $token")
        client.enqueue(object : Callback<StoryResponse>{
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                _isLoading.value = false
                if (response.isSuccessful){
                    _listStory.value = response.body()?.listStory
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
 */


    fun postStory(token: String, imgFile: File, desc: String){
        _isLoading.value = true
        val description = desc.toRequestBody("text/plain".toMediaType())
        val photoFile = imgFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val img = MultipartBody.Part.createFormData("photo", imgFile.name, photoFile)

        val client = ApiConfig.getApiService().postNewStory(bearer = "Bearer $token", img, description)
        client.enqueue(object : Callback<AddNewStoryResponse>{
            override fun onResponse(call: Call<AddNewStoryResponse>, response: Response<AddNewStoryResponse>) {
                _isLoading.value = false
                when (response.code()) {
                    401 -> "${response.code()} : Bad Request"
                    403 -> "${response.code()} : Forbidden"
                    404 -> "${response.code()} : Not Found"
                    else -> "${response.code()} : ${response.message()}"
                }
            }

            override fun onFailure(call: Call<AddNewStoryResponse>, t: Throwable) {
                _isLoading.value = false
                Log.e(tag, "onFailure: ${t.message}")
            }

        })
    }
}