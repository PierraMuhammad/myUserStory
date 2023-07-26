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
import com.dicoding.picodiploma.mystoryapp.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.mystoryapp.model.LoginViewModel
import com.dicoding.picodiploma.mystoryapp.model.ViewModelFactory


class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()

        binding?.register?.setOnClickListener {
            val intentToRegister = Intent(this, RegisterActivity::class.java)
            startActivity(intentToRegister)
            finish()
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

    private fun setupViewModel() {
        loginViewModel = ViewModelProvider(this, ViewModelFactory(UserPreferenceDatastore.getInstance(dataStore), this))[LoginViewModel::class.java]

        loginViewModel.let { viewModel ->
            viewModel.loginResult.observe(this) { login ->
                viewModel.saveUser(
                    login.loginResult.name,
                    login.loginResult.userId,
                    login.loginResult.token
                )
            }

            viewModel.message.observe(this) { message ->
                if (message == "200") {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Good News")
                    builder.setMessage("Login Berhasil :)")
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                        val intentToMain = Intent(this, MainActivity::class.java)
                        intentToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intentToMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intentToMain)
                        finish()
                    }, 2000L)
                }
            }

            viewModel.error.observe(this) { error ->
                if (error == "400") {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Bad News")
                    builder.setMessage("Email Tidak Valid :(")
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                    }, 2000L)
                }
                if (error == "401") {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("Bad News")
                    builder.setMessage("User Tidak Ada :(")
                    val alertDialog: AlertDialog = builder.create()
                    alertDialog.setCancelable(false)
                    alertDialog.show()
                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                    }, 2000L)
                }
            }
            viewModel.isLoading.observe(this) { isLoading ->
                progressValue(isLoading)
            }
        }
    }

    private fun setupAction() {
        binding?.loginButton?.setOnClickListener {
            val email = binding?.emailEditTextLayout?.text.toString()
            val password = binding?.passwordEditTextLayout?.text.toString()
            when {
                email.isEmpty() -> {
                    binding?.emailEditTextLayout?.error = "Email harus diisi"
                }
                password.isEmpty() -> {
                    binding?.passwordEditTextLayout?.error = "Password harus diisi"
                }
                password.length < 8 -> {
                    binding?.passwordEditTextLayout?.error = "Password harus lebih dari 8 karakter"
                }
                else -> {
                    loginViewModel.login(email, password)
                }
            }
        }
    }

    private fun playAnimation() {
        val login = ObjectAnimator.ofFloat(binding?.titleTextView, View.ALPHA, 1f).setDuration(500L)
        val msg = ObjectAnimator.ofFloat(binding?.messageTextView, View.ALPHA, 1f).setDuration(500L)
        val txtEmail = ObjectAnimator.ofFloat(binding?.emailTextView, View.ALPHA, 1f).setDuration(500L)
        val inputEmail = ObjectAnimator.ofFloat(binding?.emailEditTextLayout, View.ALPHA, 1f).setDuration(500L)
        val txtPassword = ObjectAnimator.ofFloat(binding?.passwordTextView, View.ALPHA, 1f).setDuration(500L)
        val inputPassword = ObjectAnimator.ofFloat(binding?.passwordEditTextLayout, View.ALPHA, 1f).setDuration(500L)
        val btnLogin = ObjectAnimator.ofFloat(binding?.loginButton, View.ALPHA, 1f).setDuration(500L)
        val btnRegister = ObjectAnimator.ofFloat(binding?.register, View.ALPHA, 1f).setDuration(500L)

        val togetherEmail = AnimatorSet().apply {
            playTogether(txtEmail, inputEmail)
        }

        val togetherPass = AnimatorSet().apply {
            playTogether(txtPassword, inputPassword)
        }

        AnimatorSet().apply {
            playSequentially(login, msg,
            togetherEmail,
            togetherPass,
            btnLogin,
            btnRegister)
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