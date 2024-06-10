package com.example.ims

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.ims.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    private lateinit var bind: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.Frame, dashboard())
                .commit()
        }
        val user = "sadmin"
//        showNotification("hello", "World")
        val bottomnav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        if (user != "admin") {
            bottomnav.menu.findItem(R.id.item_4).setIcon(R.drawable.pin_drop_24px)
            bottomnav.menu.findItem(R.id.item_4).setTitle(R.string.location)

        }
        bottomnav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.item_1 -> {
                    replacefragement(dashboard(), "dashboard")
//                    loadFragment(dashboard())
                    true
                }

                R.id.item_2 -> {
                    replacefragement(inventory(), "inventory")
//                    loadFragment(inventory())
                    true
                }

                R.id.item_3 -> {
//                    loadFragment(product())
                    replacefragement(product(), "product")
                    true
                }

                R.id.item_4 -> {

                    if (user == "admin") {

                    } else {
                        replacefragement(MapsFragment(), "maps")
                    }

                    true
                }

                else -> false
            }
        }
    }

    fun replacefragement(fragment: Fragment, tag: String) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        FragmentTransaction.TRANSIT_ENTER_MASK
        fragmentTransaction.replace(R.id.Frame, fragment)
        fragmentTransaction.commit()


    }

//    override fun onBackPressed() {
//        // If there are fragments in the back stack, pop the back stack
//        if (supportFragmentManager.backStackEntryCount > 0) {
//            supportFragmentManager.popBackStack()
//        } else {
//            super.onBackPressed()
//        }
//
//    }

//    private fun updateBottomNavigationSelection() {
//        val currentFragment = supportFragmentManager.findFragmentById(R.id.Frame)
//        when (currentFragment?.tag) {
//            "dashboard" -> bind.bottomNavigation.selectedItemId = R.id.item_1
//            "inventory" -> bind.bottomNavigation.selectedItemId = R.id.item_2
//        }
//    }

    private fun showNotification(title: String, message: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = System.currentTimeMillis().toInt()
        // Create an intent for the activity you want to open when the notification is clicked
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        // You can add extras to the intent if you need to pass data to the activity

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "my_channel_id"
            val channel =
                NotificationChannel(channelId, "My Channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.notification_add_24px)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent) // Attach the pending intent to the notification
                .build()

            notificationManager.notify(notificationId, notification)
        } else {
            val notification = NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.notification_add_24px)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent) // Attach the pending intent to the notification
                .build()

            notificationManager.notify(notificationId, notification)
        }
    }
}

