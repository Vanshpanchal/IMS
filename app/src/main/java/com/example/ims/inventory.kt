package com.example.ims

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ims.databinding.FragmentInventoryBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.math.log

class inventory : Fragment() {
    lateinit var binding: FragmentInventoryBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInventoryBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val inventory = listOf(
            inventory_list("Inventory 1", "25-01-2023", "01"),
            inventory_list("Inventory 8", "01-02-2023", "08"),
            inventory_list("Inventory 7", "31-01-2023", "03"),
            inventory_list("Inventory 3", "27-01-2023", "04")
        )

        val recyclerView = binding.rvInventory
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val inventoryAdapter = inventory_adapter(inventory)
        binding.rvInventory.adapter = inventoryAdapter

        inventoryAdapter.onItem(object : inventory_adapter.onitemclick {
            override fun itemClickListener(position: Int) {
                Log.d("hello", "itemClickListener: ${position}")
                val frag = specific_inventory()
                val bundle = Bundle()
                bundle.putString("hello","${inventory[position].inventoryName}")
                frag.arguments = bundle
                (activity as? MainActivity)?.replacefragement(frag)
            }

        })
        val currentDate = Date()
        val formatter = SimpleDateFormat("dd/MM/yyyy")
        val formattedDate = formatter.format(currentDate)
        binding.addInv.setOnClickListener {
            val view = LayoutInflater.from(requireActivity())
                .inflate(R.layout.inventory_dialog, null, false)
            val dialog = MaterialAlertDialogBuilder(
                requireContext()
            )
                .setView(view)
                .create()

            view.findViewById<TextInputEditText>(R.id.date).text = Editable.Factory.getInstance().newEditable(formattedDate)
            view.findViewById<Button>(R.id.submit_btn).setOnClickListener {
                Log.d("hello", "itemClickListener: $} ")
                dialog.dismiss()
            }

            dialog.show()
        }
    }
}
