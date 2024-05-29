package com.example.ims

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.ims.databinding.ActivityHomeBinding
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Home_act : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityHomeBinding
    val Emulator_URL  = "http://10.0.2.2:8000"
    val Device_URL = "http://ip-address-of-laptop:8080"  // ip-address on Laptop

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signIn.setOnClickListener {
            val intent = Intent(this, Signin_act::class.java)
            startActivity(intent)
            finish()
        }

        binding.singUp.setOnClickListener {
            val intent = Intent(this, signup_act::class.java)
            startActivity(intent)
            finish()
        }
        val gson = GsonBuilder().setLenient().create()
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl(Emulator_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getProductData()
//        retrofitData.enqueue(object : Callback<MyData?> {
//            override fun onResponse(call: Call<MyData?>, response: Response<MyData?>) {
//                val response = response.body()
//                Log.d("hello", "onSuccess: " +response)
//                Log.d("hello", "onSuccess: " +response)
//
//            }
//
//            override fun onFailure(call: Call<MyData?>, t: Throwable) {
//                Log.d("hello", "onFailure: " + t.message)
//            }
//
//        })

        val retrofitData2 = retrofitBuilder.setup()
        retrofitData2.enqueue(object : Callback<String?> {
            override fun onResponse(call: Call<String?>, response: Response<String?>) {
                val response = response.body()
                Log.d("hello", "onSuccess: " +response)
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                Log.d("hello", "onFailure: " + t.message)
            }

        })

        val data = M_product("Mobile Burger",5,120)
        val retrofitData3 = retrofitBuilder.createsProductData(data)
        retrofitData3.enqueue(object : Callback<M_product> {
            override fun onResponse(call: Call<M_product>, response: Response<M_product>) {
                val response = response.body()
                Log.d("hello", "onSuccess: " +response)
            }

            override fun onFailure(call: Call<M_product>, t: Throwable) {
                Log.d("hello", "onFailure: " + t.message)
            }


        })


    }
}