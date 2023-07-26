package com.dicoding.picodiploma.mystoryapp.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.mystoryapp.R
import com.dicoding.picodiploma.mystoryapp.data.response.UserPreferenceDatastore
import com.dicoding.picodiploma.mystoryapp.data.response.dataStore
import com.dicoding.picodiploma.mystoryapp.databinding.ActivityAddStoryBinding
import com.dicoding.picodiploma.mystoryapp.model.LoginViewModel
import com.dicoding.picodiploma.mystoryapp.model.MainViewModel
import com.dicoding.picodiploma.mystoryapp.model.ViewModelFactory
import androidx.datastore.preferences.core.Preferences
import com.dicoding.picodiploma.mystoryapp.convertUriToFile
import com.dicoding.picodiploma.mystoryapp.createCustomFile
import com.dicoding.picodiploma.mystoryapp.reduceFileImage
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "User")

class AddStoryActivity : AppCompatActivity() {
    private var _binding: ActivityAddStoryBinding? = null
    private val binding get() = _binding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var photoPath: String

    private var getFile: File? = null
    
    companion object{
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10) {
            if (!permissionsGranted()){
                Toast.makeText(this, "Izin TIdak diberikan", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun permissionsGranted() = REQUIRED_PERMISSIONS.all { 
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = getString(R.string.add_new_story)

        mainViewModel = ViewModelProvider(this, ViewModelFactory(UserPreferenceDatastore.getInstance(dataStore), this))[MainViewModel::class.java]
        loginViewModel = ViewModelProvider(this, ViewModelFactory(UserPreferenceDatastore.getInstance(dataStore), this))[LoginViewModel::class.java]

        if (!permissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                10
            )
        }

        binding?.apply {
            btnAddGalery.setOnClickListener { takePhotoFromGallery() }
            btnAddCamera.setOnClickListener { takePhotoFromCamera() }
            buttonAdd.setOnClickListener { uploadImage() }
        }
    }

    private fun takePhotoFromGallery(){
        val intentToGallery = Intent()
        intentToGallery.action = ACTION_GET_CONTENT
        intentToGallery.type = "image/*"
        val pickPhoto = Intent.createChooser(intentToGallery, getString(R.string.pick_picture))
        launcherIntentGallery.launch(pickPhoto)
    }

    private fun takePhotoFromCamera(){
        val intentToCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intentToCamera.resolveActivity(packageManager)

        createCustomFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(this@AddStoryActivity, "com.dicoding.picodiploma.mystoryapp", it)
            photoPath = it.absolutePath
            intentToCamera.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intentToCamera)
        }
    }

    private fun uploadImage(){
        if (getFile != null){
            if (binding?.edAddDescription?.text.toString().isNotEmpty()){
                val file = reduceFileImage(getFile as File)
                loginViewModel.getUser().observe(this){user->
                    mainViewModel.postStory(user.token, file, binding?.edAddDescription?.text.toString())
                    mainViewModel.isLoading.observe(this){
                        showLoading(it)
                    }
                }
            } else {
                Toast.makeText(this@AddStoryActivity, getString(R.string.desc_not_empty), Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this@AddStoryActivity, getString(R.string.img_not_empty), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(isLoading: Boolean){
        if (isLoading){
            binding?.progressBar?.visibility = View.VISIBLE
        } else {
            binding?.progressBar?.visibility = View.GONE

            val intentToMain = Intent(this@AddStoryActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intentToMain)
            finish()
        }
    }

    private val launcherIntentGallery = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->
        if (result.resultCode == RESULT_OK){
            val selectedImg: Uri = result.data?.data as Uri
            val file =  convertUriToFile(selectedImg, this@AddStoryActivity)
            getFile = file

            binding?.tvAddImg?.setImageURI(selectedImg)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            val file = File(photoPath)
            file.let { file ->
                getFile = file
                binding?.tvAddImg?.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }
}