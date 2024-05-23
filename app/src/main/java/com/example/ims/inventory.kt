package com.example.ims

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ims.databinding.FragmentDashboardBinding
import com.example.ims.databinding.FragmentInventoryBinding

class inventory : Fragment() {
    lateinit var binding: FragmentInventoryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInventoryBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

}