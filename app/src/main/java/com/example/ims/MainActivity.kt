package com.example.ims

import android.os.Bundle
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
        replacefragement(dashboard())
       val bottomnav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomnav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.item_1 -> {
                    replacefragement(dashboard())
                }
                R.id.item_2 -> {
                    replacefragement(inventory())
                }
            }
            true
        }
    }
    private fun replacefragement(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        FragmentTransaction.TRANSIT_ENTER_MASK
        fragmentTransaction.replace(R.id.Frame, fragment)
        fragmentTransaction.commit()

    }
}

