package com.dicoding.picodiploma.mystoryapp.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.mystoryapp.R
import com.dicoding.picodiploma.mystoryapp.adapter.MainAdapter
import com.dicoding.picodiploma.mystoryapp.data.response.UserPreferenceDatastore
import com.dicoding.picodiploma.mystoryapp.data.response.dataStore
import com.dicoding.picodiploma.mystoryapp.databinding.ActivityMainBinding
import com.dicoding.picodiploma.mystoryapp.model.ListStoryViewModel
import com.dicoding.picodiploma.mystoryapp.model.LoginViewModel
import com.dicoding.picodiploma.mystoryapp.model.MainViewModel
import com.dicoding.picodiploma.mystoryapp.model.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "User")

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var listStoryViewModel: ListStoryViewModel
    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Story App"

        mainViewModel = ViewModelProvider(this, ViewModelFactory(UserPreferenceDatastore.getInstance(dataStore), this))[MainViewModel::class.java]
        loginViewModel = ViewModelProvider(this, ViewModelFactory(UserPreferenceDatastore.getInstance(dataStore), this))[LoginViewModel::class.java]
        listStoryViewModel = ViewModelProvider(this, ViewModelFactory(UserPreferenceDatastore.getInstance(dataStore), this))[ListStoryViewModel::class.java]

        adapter = MainAdapter()
        binding.rvListStory.adapter = adapter

        mainViewModel.isLoading.observe(this){
            progressValue(it)
        }

        var authToken = ""

        loginViewModel.getUser().observe(this){
            if (it.userId.isEmpty()){
                val intentToLogin = Intent(this, LoginActivity::class.java)
                startActivity(intentToLogin)
            }
            else{
                authToken = it.token.toString()
                print("token saat di main: ${authToken.toString()}\n")
                listStoryViewModel.getStory(authToken).observe(this){
                    if(it == null){
                        print("data getStory adalah $it\n")
                    }
                    adapter.submitData(lifecycle, it)
                }
            }
        }

        val layoutManager = LinearLayoutManager(this)
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)

        binding.apply {
            rvListStory.layoutManager = layoutManager
            rvListStory.addItemDecoration(itemDecoration)
            addStory.setOnClickListener {
                val intentToAddStory = Intent(this@MainActivity, AddStoryActivity::class.java)
                startActivity(intentToAddStory)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_logout -> {
                loginViewModel.signout()
            }
            R.id.action_map -> {
                val intentToMaps = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(intentToMaps)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun progressValue(isLoading: Boolean){
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}