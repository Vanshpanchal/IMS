package com.example.ims

class ApiResponse {
    data class User(
        val _id: String,
        val userName: String,
        val email: String,
        val mobileNo: String,
        val Imageurl: String,
        val createdAt: String,
        val updatedAt: String,
        val __v: Int,
        val role: String
    )

    data class Data(
        val user: User?,
        val accesstoken: String?,
        val refreshtoken: String?
    )

    data class ApiResponse(
        val statusCode: Int,
        val data: Data?,
        val message: String,
        val success: Boolean
    )
}
