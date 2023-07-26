package com.dicoding.picodiploma.mystoryapp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.mystoryapp.R
import com.dicoding.picodiploma.mystoryapp.data.response.UserPreferenceDatastore
import com.dicoding.picodiploma.mystoryapp.databinding.ActivitySplashscreenBinding
import com.dicoding.picodiploma.mystoryapp.model.LoginViewModel
import com.dicoding.picodiploma.mystoryapp.model.MainViewModel
import com.dicoding.picodiploma.mystoryapp.model.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "User")

class Splashscreen : AppCompatActivity() {
    private var _binding: ActivitySplashscreenBinding? = null
    private val binding get() = _binding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySplashscreenBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.hide()

        loginViewModel = ViewModelProvider(this, ViewModelFactory(UserPreferenceDatastore.getInstance(dataStore), this))[LoginViewModel::class.java]

        loginViewModel.getUser().observe(this){
            if (it.userId.isEmpty()){
                Handler().postDelayed({
                    val intentToLogin = Intent(this@Splashscreen, LoginActivity::class.java)
                    startActivity(intentToLogin)
                    finish()
                }, 2000L)
            } else{
                Handler().postDelayed({
                    val intentToMain = Intent(this@Splashscreen, MainActivity::class.java)
                    startActivity(intentToMain)
                    finish()
                }, 2000L)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}