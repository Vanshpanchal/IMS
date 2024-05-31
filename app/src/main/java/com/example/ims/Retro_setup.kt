package com.example.ims

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retro_setup {
    val Emulator_URL  = "http://10.0.2.2:8000"
    val Device_URL = "http://ip-address-of-machine:8080"
    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Emulator_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiInterface by lazy {
        instance.create(ApiInterface::class.java)
    }
}