package com.example.ims

import com.google.firebase.Timestamp

data class Cordinate(
    val Address: String? = null,
    val CreatedAt : Timestamp? = null,
    val Latitude: Double? = null,
    val Longitude: Double? = null,
    val UserID: String? = null
)
