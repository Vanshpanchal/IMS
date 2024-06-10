package com.example.ims

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class StockMonitorService : Service() {
    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)
    private val fs = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        job = scope.launch {
            while (isActive) {
                // Replace "stocks" with your Firestore collection name
                monitorStocks()

                delay(600) // Check every minute
            }
        }
        return START_STICKY
    }

    private suspend fun monitorStocks() {
        val stockRef =
            fs.collection("Product").document(auth.currentUser?.uid!!).collection("MyProduct")

        stockRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("D_CHECK", "Listen failed", e)
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                for (doc in snapshot.documents) {
                    val stock = doc.toObject(inv_itemsItem::class.java)
                    if (stock != null && stock.Stock!!.toInt() < 5) {
                        // If stock is below 5, call showNotification function
                        Log.d("D_CHECK", "monitorStocks: Low Stock")
//                        showNotification(
//                            "Low Stock Alert",
//                            "Stock for ${stock.ItemName} is below 5"
//                        )
                    }
                }
            } else {
                Log.d("Hello", "No stock data")
            }
        }
    }

}