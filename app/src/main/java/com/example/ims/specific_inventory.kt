package com.example.ims

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class specific_inventory : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_specific_inventory, container, false)

        // Retrieve the data from the arguments
        val message = arguments?.getString("hello")
        Log.d("HELLO", "onCreateView: ${message}")
        val textView = view.findViewById<TextView>(R.id.textView)
        textView.text = message

        return view
        }




}