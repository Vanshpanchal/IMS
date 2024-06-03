package com.example.ims

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.example.ims.databinding.ActivityHomeBinding

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
            val intent = Intent(this, SigninAct::class.java)
            startActivity(intent)
            finish()
        }

        binding.singUp.setOnClickListener {
            val intent = Intent(this, SignupAct::class.java)
            startActivity(intent)
            finish()
        }
        // API
//        val gson = GsonBuilder().setLenient().create()
//        val retrofitBuilder = Retrofit.Builder()
//            .baseUrl(Emulator_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ApiInterface::class.java)
//
//        val user_data = Login("henryromero1609@gmial.com" ,"henry@0908")
//        val ref = retrofitBuilder.login(user_data)
//
//        ref.enqueue(object: Callback<m_login>{
//            override fun onResponse(call: Call<m_login>, response: Response<m_login>) {
//                val a = response.body()
//
//                    Log.d("hello", "onSuccess: " +a)
//
//            }
//
//            override fun onFailure(call: Call<m_login>, t: Throwable) {
//                Log.d("hello", "onFailure: " + t.message)
//            }
//
//        })


//        val retrofitData = retrofitBuilder.getProductData()
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

//        val retrofitData2 = retrofitBuilder.setup()
//        retrofitData2.enqueue(object : Callback<String?> {
//            override fun onResponse(call: Call<String?>, response: Response<String?>) {
//                val response = response.body()
//                Log.d("hello", "onSuccess: " +response)
//            }
//
//            override fun onFailure(call: Call<String?>, t: Throwable) {
//                Log.d("hello", "onFailure: " + t.message)
//            }
//
//        })

//        val data = M_product("Mobile Burger",5,120)
//        val retrofitData3 = retrofitBuilder.createsProductData(data)
//        retrofitData3.enqueue(object : Callback<M_product> {
//            override fun onResponse(call: Call<M_product>, response: Response<M_product>) {
//                val response = response.body()
//                Log.d("hello", "onSuccess: " +response)
//            }
//
//            override fun onFailure(call: Call<M_product>, t: Throwable) {
//                Log.d("hello", "onFailure: " + t.message)
//            }
//
//
//        })


    }
}