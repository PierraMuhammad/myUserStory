package com.dicoding.picodiploma.mystoryapp.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.mystoryapp.data.API.ApiConfig
import com.dicoding.picodiploma.mystoryapp.data.response.RegisterResponse
import com.dicoding.picodiploma.mystoryapp.data.response.UserPreferenceDatastore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel(private val pref: UserPreferenceDatastore): ViewModel(){
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val error = MutableLiveData("")
    val message = MutableLiveData("")
    private val tag = RegisterViewModel::class.java.simpleName

    fun register(name: String, email: String, password: String){
        print("""
            name: ${name},
            email: ${email},
            password: ${password}
        """.trimIndent() + "\n")
        _isLoading.value = true
        val client = ApiConfig.getApiService().Register(name, email, password)
        client.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                print(response.message() + "\n" + response.code()+ "\n")
                when(response.code()){
                    400 -> error.postValue("400")
                    201 -> message.postValue("201")
                    else -> error.postValue("ERROR ${response.code()}: ${response.errorBody()}")
                }
                _isLoading.value = false
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _isLoading.value = true
                Log.e(tag, "onFailure Call: ${t.message}")
                error.postValue(t.message)
            }

        })
    }
}