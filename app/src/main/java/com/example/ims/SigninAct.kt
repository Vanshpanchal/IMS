package com.example.ims

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.example.ims.databinding.ActivitySigninBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject

class SigninAct : AppCompatActivity() {
    lateinit var binding: ActivitySigninBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var auth: FirebaseAuth
    private lateinit var fs: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        fs = FirebaseFirestore.getInstance()

        binding.bSignIn.setOnClickListener {
            finish()
            Log.d("hello", "onCreate: clicked")
        }
        sharedPreferences = getSharedPreferences("USERDATA", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        if (sharedPreferences.contains("Email") && sharedPreferences.contains("Pass")) {
            authenticateUser(
                sharedPreferences.getString("Email", "").toString(),
                sharedPreferences.getString("Pass", "").toString()
            )
        }
        binding.frgPassword.setOnClickListener {
            if (binding.email.text.isNullOrBlank()) {
                resetPassword(binding.email.text.toString())
            }
        }

        binding.bSignIn.setOnClickListener{
            val intent = Intent(this, Home_act::class.java)
            startActivity(intent)
        }

        binding.LoginBtn.setOnClickListener {
//            signIn("visualcode780@gmail.com", "123456789")
            signIn(binding.email.text.toString(), binding.password.text.toString())
//            api_login()
        }

    }

    private fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                val bar = Snackbar.make(
                    binding.root,
                    "Reset Password Mail Sent",
                    Snackbar.LENGTH_SHORT
                )
                bar.setBackgroundTint(getColor(R.color.blue))
                bar.setActionTextColor(getColor(R.color.blue3))
                bar.setAction("Ok") {
                    bar.dismiss()
                }
                bar.show()
            } else {
                Log.d("D_CHECK", "resetPassword: ${it.exception?.message}")
            }
        }
    }
    private fun signIn(email: String, pass: String) {
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            val user = auth.currentUser
            if (it.isSuccessful) {
                if (user!!.isEmailVerified) {
                    editor.clear()
                    editor.putString("Email", email)
                    editor.putString("Pass", pass)
                    editor.commit()
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
                    bar.setAction("Ok") {
                        bar.dismiss()
                    }
                    bar.show()
                    sendEmailVerification()
                }
            } else {
                when (it.exception) {
                    is FirebaseAuthInvalidUserException -> {
                        val bar = Snackbar.make(
                            binding.root,
                            "Email is not Register",
                            Snackbar.LENGTH_SHORT
                        )
                        bar.setAction("Ok") {
                            bar.dismiss()
                        }
                        bar.setBackgroundTint(getColor(R.color.blue))
                        bar.setActionTextColor(getColor(R.color.blue3))
                        bar.show()
                    }

                    else -> {
                        val bar = Snackbar.make(
                            binding.root,
                            "Invalid Credential",
                            Snackbar.LENGTH_SHORT
                        )
                        bar.setAction("Ok") {
                            bar.dismiss()

                        }
                        bar.setBackgroundTint(getColor(R.color.blue))
                        bar.setActionTextColor(getColor(R.color.blue3))
                        bar.show()
                        Log.d("hello", "Auth ${it.exception}")
                    }
                }
                Log.d("D_CHECK", "signIn: ${it.exception?.message}")
            }
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

    private fun authenticateUser(emailAddress: String, pass: String) {
        auth.signInWithEmailAndPassword(emailAddress, pass)
            .addOnCompleteListener { it ->
                val user = auth.currentUser
                if (it.isSuccessful) {
                    if (user != null && user.isEmailVerified) {

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)

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

    private fun api_login() {

        val jsonBody = JSONObject()
        jsonBody.put("email", "henryromero1609@gmail.com")
        jsonBody.put("password", "henry@0908")

        val url = "http://10.0.2.2:8000/users/login"
        val postRequest = JsonObjectRequest(
            Request.Method.POST, url, jsonBody,
            { response ->
                val code = response.get("statusCode")
                Log.d(
                    "API_CHECK",
                    "onCreate: ${response.getJSONObject("data").getString("accesstoken")}"
                )
                if (code == 200) {
                    val u_id = response.getJSONObject("data").getJSONObject("user").getString("_id")
                    val access_token = response.getJSONObject("data").getString("accesstoken")
                    editor.clear()
                    editor.putString("U_ID", u_id)
                    editor.putString("ACCESS_TOKEN", access_token)
                    editor.commit()
                    val intent = Intent(this@SigninAct, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            },
            { error ->
                // Handle error
                Log.d("API_CHECK", "FonCreate: ${String(error.networkResponse.data)}")

            }
        )
        MySingleton.getInstance(this).addToRequestQueue(postRequest)

//        Retro_setup.apiService.login(user_data).enqueue(object : Callback<m_login> {
//            override fun onResponse(call: Call<m_login>, response: Response<m_login>) {
//                val result = response.body()?.statusCode
//                val u_id = response.body()?.data?.user?._id
//                Log.d("API_CHECK", "onResponse: $result")
//                if (result == 200) {
//                    editor.clear()
//                    editor.putString("U_ID", u_id)
//                    editor.putString("ACCESS_TOKEN", response.body()?.data?.accesstoken)
//                    editor.commit()
//                    val intent = Intent(this@Signin_act, MainActivity::class.java)
//                    startActivity(intent)
//                    finish()
//                }
//            }
//
//            override fun onFailure(call: Call<m_login>, t: Throwable) {
//                Log.d("API_CHECK", "onFailure: ${t.message}")
//            }
//
//        })
    }
}