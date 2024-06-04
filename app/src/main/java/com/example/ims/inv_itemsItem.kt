package com.example.ims

import com.google.firebase.Timestamp

data class inv_itemsItem(
    val Category: String? = null,
    val InventoryId: String? = null,
    val ItemName: String? = null,
    val PricePerUnit: String? = null,
    val ProductId: String? = null,
    val Stock: String? = null,
    val CreatedAt: Timestamp? = null

)