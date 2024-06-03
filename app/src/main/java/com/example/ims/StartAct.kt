package com.example.ims

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ims.databinding.ActivityStartBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class StartAct : AppCompatActivity() {
    lateinit var binding: ActivityStartBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var auth: FirebaseAuth
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
        } else {
            binding.animationView.visibility = View.VISIBLE
            auth = FirebaseAuth.getInstance()

            sharedPreferences = getSharedPreferences("USERDATA", Context.MODE_PRIVATE)
            editor = sharedPreferences.edit()

            if (sharedPreferences.contains("Email") && sharedPreferences.contains("Pass")) {
                authenticateUser(
                    sharedPreferences.getString("Email", "").toString(),
                    sharedPreferences.getString("Pass", "").toString()
                )
            } else {
                val intent = Intent(this, Home_act::class.java)
                startActivity(intent)
                finish()
            }
        }

    }

    private fun authenticateUser(emailAddress: String, pass: String) {
        auth.signInWithEmailAndPassword(emailAddress, pass)
            .addOnCompleteListener {
                val user = auth.currentUser
                if (it.isSuccessful) {
                    if (user != null && user.isEmailVerified) {

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        val bar = Snackbar.make(
                            binding.root,
                            "Verify Email",
                            Snackbar.LENGTH_SHORT
                        )
                        bar.setBackgroundTint(getColor(R.color.blue))
                        bar.setActionTextColor(getColor(R.color.blue3))
                        bar.setAction("OK") {
                            bar.dismiss()
                        }
                        bar.show()
                        sendEmailVerification()
                    }
                } else {
                    Log.d("hello", "onCreate: ${it.exception?.message} ")
                }
            }.addOnFailureListener {
                val bar = Snackbar.make(
                    binding.root,
                    "Please Check Your Entered Credentials",
                    Snackbar.LENGTH_SHORT
                )
                bar.setAction("OK") {
                    bar.dismiss()
                }
                bar.setBackgroundTint(getColor(R.color.blue))
                bar.setActionTextColor(getColor(R.color.blue3))
                bar.show()
                Log.d("hello", "onCreate: ${it.message} ")
            }
    }
    private fun sendEmailVerification() {
        val user = auth.currentUser

        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val bar = Snackbar.make(binding.root, "Mail Sent", Snackbar.LENGTH_SHORT)
                    bar.setBackgroundTint(getColor(R.color.blue))
                    bar.setAction("OK") {
                        bar.dismiss()
                        val intent = Intent(this, SigninAct::class.java)
                        startActivity(intent)
                        finish()
                    }
                    bar.setActionTextColor(getColor(R.color.blue3))
                    bar.show()
                } else {
                    Log.d("D_CHECK", "sendEmailVerification: $task.exception?.message")
                }
            }?.addOnFailureListener {
                val bar = Snackbar.make(binding.root, "An Error Occurred", Snackbar.LENGTH_SHORT)
                bar.setBackgroundTint(getColor(R.color.blue))
                bar.setAction("OK") {
                    bar.dismiss()
                }
                bar.setActionTextColor(getColor(R.color.blue3))
                bar.show()
            }
    }
    private fun isInternetAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
//        return false
    }
    private fun showNoInternetDialog() {
        MaterialAlertDialogBuilder(this@StartAct)
            .setTitle("No Internet Connection")
            .setMessage("Please check your internet connection and try again.")
            .setPositiveButton("OK") { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }


}