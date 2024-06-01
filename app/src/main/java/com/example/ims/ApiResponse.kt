package com.example.ims

class ApiResponse<T>(
        val statusCode: String,
        val data: T?,
        val message: String,
        val success: Boolean

)
