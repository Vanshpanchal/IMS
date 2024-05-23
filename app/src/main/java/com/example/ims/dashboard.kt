package com.example.ims

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.example.ims.databinding.FragmentDashboardBinding


class dashboard : Fragment() {
    lateinit var binding: FragmentDashboardBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val msg = arrayOf("Packages to be packed", "Packages to be shipped", "Packages to be delivered", "Packages to be Invoiced")
        val num = arrayOf("450", "21", "10", "24")
        val images = arrayOf(
            R.drawable.logistics,
            R.drawable.logistics,
            R.drawable.logistics,
            R.drawable.logistics
        )
        // Adapter
        val adapter = list_view_adapter(requireContext(), R.layout.list_view, msg, num, images)

        // ListView
        val listView: ListView = binding.listView
        listView.dividerHeight = 0
        listView.adapter = adapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedItem = msg[position]
            Toast.makeText(requireContext(), "Selected Item: $selectedItem", Toast.LENGTH_SHORT)
                .show()
//        }

        }
//        listView.adapter = adapter
        return binding.root

    }

}
