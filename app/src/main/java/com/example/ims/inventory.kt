package com.example.ims

import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ims.databinding.FragmentInventoryBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.math.log

class inventory : Fragment() {
    lateinit var binding: FragmentInventoryBinding
    lateinit var inventory: ArrayList<inventory_list>
    lateinit var previewDialog: BottomSheetDialog
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
        inventory = arrayListOf()

        val a = inventory_list("Inventory 1", "25-01-2023", "01", "Owner 1")
        val b = inventory_list("Inventory 8", "01-02-2023", "08", "Owner 2")
        val c = inventory_list("Inventory 7", "31-01-2023", "03", "Owner 3")
        val d = inventory_list("Inventory 3", "27-01-2023", "04", "Owner 4")
        inventory.add(a)
        inventory.add(b)
        inventory.add(c)
        inventory.add(d)

        previewDialog = BottomSheetDialog(requireContext())
        val recyclerView = binding.rvInventory
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        load_data()
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

            view.findViewById<TextInputEditText>(R.id.date).text =
                Editable.Factory.getInstance().newEditable(formattedDate)
            val inv_name = view.findViewById<TextInputEditText>(R.id.i_name).text
            val owner = view.findViewById<TextInputEditText>(R.id.i_owner).text
            val id = view.findViewById<TextInputEditText>(R.id.i_id).text
            view.findViewById<Button>(R.id.submit_btn).setOnClickListener {
                Log.d("hello", "itemClickListener: $} ")
                dialog.dismiss()
                val data = inventory_list(
                    inv_name.toString(),
                    formattedDate,
                    id.toString(),
                    "Owner-Default"
                )
                inventory.add(data)

                load_data()
//                inventoryAdapter.sub
            }

            dialog.show()
        }
    }

    private fun load_data() {
        var inventoryAdapter = inventory_adapter(inventory)
        binding.rvInventory.adapter = inventoryAdapter

        inventoryAdapter.onItem(object : inventory_adapter.onitemclick {
            override fun itemClickListener(position: Int) {
                Log.d("hello", "itemClickListener: ${position}")

                val frag = specific_inventory()
                val bundle = Bundle()
                bundle.putString("hello", "${inventory[position].inventoryName}")
                frag.arguments = bundle
                (activity as? MainActivity)?.replacefragement(frag,"specific_inventory")
            }

        })

        inventoryAdapter.onItem_1(object : inventory_adapter.OnItemLongClickListener {
            override fun onItemLongClick(position: Int): Boolean {
                Log.d("HELLO", "onItemLongClick: ${position}")
                val view =
                    View.inflate(requireContext(), R.layout.preview_dialog_2, null)
                previewDialog.setContentView(view)
                previewDialog.show()
                previewDialog.setCancelable(true)
                previewDialog.setCanceledOnTouchOutside(true)

                view.findViewById<TextView>(R.id.inv_name).text = inventory[position].inventoryName
                view.findViewById<TextView>(R.id.inv_owner).text = inventory[position].owner_name
                view.findViewById<TextView>(R.id.created_date).text = inventory[position].created_date
                view.findViewById<TextView>(R.id.inv_id).text = inventory[position].inventory_id
                return true
            }

        })
    }
}
