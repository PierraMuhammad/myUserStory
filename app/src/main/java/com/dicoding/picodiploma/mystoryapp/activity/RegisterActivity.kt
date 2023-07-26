package com.dicoding.picodiploma.mystoryapp.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.dicoding.picodiploma.mystoryapp.data.response.UserPreferenceDatastore
import com.dicoding.picodiploma.mystoryapp.data.response.dataStore
import com.dicoding.picodiploma.mystoryapp.databinding.ActivityRegisterBinding
import com.dicoding.picodiploma.mystoryapp.model.RegisterViewModel
import com.dicoding.picodiploma.mystoryapp.model.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()

        binding?.login?.setOnClickListener {
            val intentToLogin = Intent(this, LoginActivity::class.java)
            startActivity(intentToLogin)
        }
    }

    private fun setupView(){
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel(){
        registerViewModel = ViewModelProvider(this, ViewModelFactory(UserPreferenceDatastore.getInstance(dataStore), this))[RegisterViewModel::class.java]

        registerViewModel.let { viewModels ->
            viewModels.message.observe(this){
                if (it == "201"){
                    val build = AlertDialog.Builder(this)
                    build.setTitle("Good News")
                    build.setMessage("Akun Berhasil Dibuat :)")
                    val alertDialog: AlertDialog = build.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                        val intentToLogin = Intent(this, LoginActivity::class.java)
                        startActivity(intentToLogin)
                    }, 2000L)
                }
            }
            viewModels.error.observe(this){
                if (it == "400"){
                    val build = AlertDialog.Builder(this)
                    build.setTitle("Bad News")
                    build.setMessage("Akun Gagal Dibuat :(")
                    val alertDialog: AlertDialog = build.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                    }, 2000L)
                }
            }
            viewModels.isLoading.observe(this){
                progressValue(it)
            }
        }
    }

    private fun setupAction(){
        binding?.regisButton?.setOnClickListener {
            val username = binding?.usernameEditTextLayout?.text.toString()
            val email = binding?.emailEditTextLayout?.text.toString()
            val password = binding?.passwordEditTextLayout?.text.toString()
            when {
                username.isEmpty() -> {
                    binding?.usernameEditTextLayout?.error = "Username harus diisi"
                }
                email.isEmpty() -> {
                    binding?.emailEditTextLayout?.error = "Email harus diisi"
                }
                password.isEmpty() -> {
                    binding?.passwordEditTextLayout?.error = "Password harus diisi"
                }
                password.length < 8 ->{
                    binding?.passwordEditTextLayout?.error = "Password minimal 8 karakter"
                }
                else -> {
                    registerViewModel.register(username, email, password)
                }
            }
        }
    }

    private fun playAnimation(){
        val regis = ObjectAnimator.ofFloat(binding?.titleTextView, View.ALPHA, 1f).setDuration(500L)
        val msg = ObjectAnimator.ofFloat(binding?.messageTextView, View.ALPHA, 1f).setDuration(500L)
        val txtName = ObjectAnimator.ofFloat(binding?.usernameTextView, View.ALPHA, 1f).setDuration(500L)
        val inputName = ObjectAnimator.ofFloat(binding?.usernameEditTextLayout, View.ALPHA, 1f).setDuration(500L)
        val txtEmail = ObjectAnimator.ofFloat(binding?.emailTextView, View.ALPHA, 1f).setDuration(500L)
        val inputEmail = ObjectAnimator.ofFloat(binding?.emailEditTextLayout, View.ALPHA, 1f).setDuration(500L)
        val txtPass = ObjectAnimator.ofFloat(binding?.passwordTextView, View.ALPHA, 1f).setDuration(500L)
        val inputPass = ObjectAnimator.ofFloat(binding?.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500L)
        val btnSignUp = ObjectAnimator.ofFloat(binding?.regisButton, View.ALPHA, 1f).setDuration(500L)
        val btnLogin = ObjectAnimator.ofFloat(binding?.login, View.ALPHA, 1f).setDuration(500L)

        val togetherName = AnimatorSet().apply {
            playTogether(txtName, inputName)
        }

        val togetherEmail = AnimatorSet().apply {
            playTogether(txtEmail, inputEmail)
        }

        val togetherPass = AnimatorSet().apply {
            playTogether(txtPass, inputPass)
        }

        AnimatorSet().apply {
            playSequentially(regis, msg,
                togetherName,
                togetherEmail,
                togetherPass,
                btnSignUp,
                btnLogin)
            start()
        }

    }

    private fun progressValue(isLoading: Boolean){
        binding?.progressBar?.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}