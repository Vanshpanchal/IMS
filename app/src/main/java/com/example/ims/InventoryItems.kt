package com.example.ims

import com.google.firebase.Timestamp

data class InventoryItems(
    var InventoryID: String? = null,
    val InventoryName: String? = null,
    val Country: String? = null,
    val MobileNo: String? = null,
    val Address: String? = null,
    val InventoryOwner: String? = null,
    val UserID: String? = null,
    val CreatedAt: Timestamp? = null,
)

