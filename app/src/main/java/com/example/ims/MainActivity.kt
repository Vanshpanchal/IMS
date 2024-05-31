package com.example.ims

import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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

}

