package com.example.ims

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ims.databinding.ActivityStartBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class start_Act : AppCompatActivity() {
    lateinit var binding: ActivityStartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityStartBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        installSplashScreen()

        if (!isInternetAvailable()) {
            binding.animationView.visibility = View.GONE
            showNoInternetDialog()
        }else{
            binding.animationView.visibility = View.VISIBLE
            binding.animationView.postDelayed({
                startActivity(Intent(this, Home_act::class.java))
                finish()
            }, 1000)
        }

    }
    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo.isConnected
        }
        return false
    }
    private fun showNoInternetDialog() {
        MaterialAlertDialogBuilder(this@start_Act)
            .setTitle("No Internet Connection")
            .setMessage("Please check your internet connection and try again.")
            .setPositiveButton("OK") { dialog, which ->
                finish()
            }
            .setCancelable(false)  // To prevent the user from dismissing the dialog by clicking outside of it
            .show()
    }
}