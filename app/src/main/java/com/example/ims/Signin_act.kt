package com.example.ims

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.ims.databinding.ActivitySigninBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            val user_data = Login("henryromero1609@gmail.com" ,"henry@0908")
            Retro_setup.apiService.login(user_data).enqueue(object : Callback<m_login>{
                override fun onResponse(call: Call<m_login>, response: Response<m_login>) {
                    val result = response.body()?.statusCode
                    val u_id = response.body()?.data?.user?._id
                    Log.d("API_CHECK", "onResponse: $result")
                    if (result == 200){
                        editor.clear()
                        editor.putString("U_ID",u_id)
                        editor.putString("ACCESS_TOKEN",response.body()?.data?.accesstoken)
                        editor.commit()
                        val intent = Intent(this@Signin_act, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onFailure(call: Call<m_login>, t: Throwable) {
                    Log.d("API_CHECK", "onFailure: ${t.message}")
                }

            })

        }


    }
}