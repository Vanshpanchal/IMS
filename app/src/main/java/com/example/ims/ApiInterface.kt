package com.example.ims

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiInterface {
    @GET("/products") // not included
    fun getProductData(): Call<MyData>
    @GET("/")
    fun setup(): Call<String>
    @POST("/api/products")
    fun createsProductData(@Body data: M_product): Call<M_product>

    @POST("/users/login")
    fun login(@Body data: Login): Call<Any>
}
