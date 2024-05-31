package com.example.ims

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {
    @GET("/products") // not included
    fun getProductData(): Call<MyData>
    @GET("/")
    fun setup(): Call<String>
    @POST("/api/products")
    fun createsProductData(@Body data: M_product): Call<M_product>

    @GET("/inventory/show")
    fun getInventory(@Header ("Authorization") token: String): Call<m_inventory>

    @POST("/items/inventoryItems")
    fun getInventoryItems(@Body inv_id :String,@Header ("Authorization") token: String) : Call<String>
    @POST("/users/login")
    fun login(@Body data: Login): Call<m_login>
}
