package com.dicoding.picodiploma.mystoryapp.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.dicoding.picodiploma.mystoryapp.DateFormat
import com.dicoding.picodiploma.mystoryapp.R
import com.dicoding.picodiploma.mystoryapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding

    companion object {
        const val NAME = "name"
        const val CREATE_AT = "create_at"
        const val DESCRIPTION = "description"
        const val PHOTO_URL = "photoUrl"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Detail Story"

        val photoUrl = intent.getStringExtra(PHOTO_URL)
        val name = intent.getStringExtra(NAME)
        val createAt = intent.getStringExtra(CREATE_AT)
        val desc = intent.getStringExtra(DESCRIPTION)

        Glide.with(binding.root.context).load(photoUrl).into(binding.ivDetailPhoto)

        binding.apply {
            tvDetailName.text = name
            tvDetailCreatedTime.text = createAt?.DateFormat()
            tvDetailDescription.text = desc
        }
    }
}