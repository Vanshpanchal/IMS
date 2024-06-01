package com.example.ims

data class m_login(
    val statusCode: Int,
    val data:  Data,
    val message: String,
    val success: Boolean
)