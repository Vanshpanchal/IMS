package com.example.ims

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.example.ims.databinding.ActivitySigninBinding
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback

class Signin_act : AppCompatActivity() {
    lateinit var binding: ActivitySigninBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bSignIn.setOnClickListener {
            finish()
            Log.d("hello", "onCreate: clicked")
        }
        sharedPreferences = getSharedPreferences("USERDATA", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        binding.LoginBtn.setOnClickListener {
            api_login()
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
                Log.d("API_CHECK", "onCreate: ${response.getJSONObject("data").getString("accesstoken")}")
                if (code == 200) {
                    val u_id = response.getJSONObject("data").getJSONObject("user").getString("_id")
                    val access_token = response.getJSONObject("data").getString("accesstoken")
                    editor.clear()
                    editor.putString("U_ID", u_id)
                    editor.putString("ACCESS_TOKEN", access_token)
                    editor.commit()
                    val intent = Intent(this@Signin_act, MainActivity::class.java)
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