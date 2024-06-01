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
import com.example.ims.databinding.FragmentDashboardBinding
import com.example.ims.databinding.FragmentProductBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.android.material.divider.MaterialDivider
import com.google.android.material.textfield.TextInputEditText
import kotlin.math.log


class product : Fragment() {
    private lateinit var productAdapter: product_adapter
    lateinit var binding: FragmentProductBinding
    lateinit var previewDialog: BottomSheetDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProductBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = binding.rvProduct
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val items = listOf(
            product_list("Product 1", "1", "200"),
            product_list("Product 2", "2", "120"),
            product_list("Product 3", "3", "14"),
            product_list("Product 4", "4", "20"),
        )

        previewDialog = BottomSheetDialog(requireContext())

        productAdapter = product_adapter(items)
        recyclerView.adapter = productAdapter

        productAdapter.onItem(object : product_adapter.onitemclick {
            override fun itemClickListener(position: Int) {
                Log.d("hello", "itemClickListener: ${position}")
                val view =
                    View.inflate(requireContext(), R.layout.preview_dialog, null)
                previewDialog.setContentView(view)
                previewDialog.show()
                previewDialog.setCancelable(true)
                previewDialog.setCanceledOnTouchOutside(true)

//                view.findViewById<TextView>(R.id.product_name).text = items[position].title
//                view.findViewById<TextView>(R.id.product_unit).text = items[position].quantity_left
//                view.findViewById<TextView>(R.id.inv_name).text = items[position].inventory_name
            }

        })



//        productAdapter.ondelete(object : product_adapter.onitemclick {
//            override fun itemClickListener(position: Int) {
//                Log.d("hello", "delete itemClickListener: ${position}")
//                MaterialAlertDialogBuilder(
//                    requireContext()
//                )
//                    .setTitle("Product Delete")
//                    .setIcon(R.drawable.delete_24px)
//                    .setMessage("You want to delete ${items[position].title}?")
//                    .setPositiveButton("Yes") { dialog, which ->
//                        dialog.dismiss()
//                    }
//                    .setNegativeButton("No") { dialog, which ->
//                        dialog.dismiss()
//                    }
//                    .show();
//
//            }
//        })
//
//        productAdapter.onedit(object : product_adapter.onitemclick {
//            override fun itemClickListener(position: Int) {
//                val view = LayoutInflater.from(requireActivity())
//                    .inflate(R.layout.edit_dialog, null, false)
//                val dialog = MaterialAlertDialogBuilder(
//                    requireContext()
//                )
//                    .setView(view)
//                    .create()
//                view.findViewById<TextInputEditText>(R.id.p_price).text =Editable.Factory.getInstance().newEditable(items[position].price)
//                view.findViewById<TextInputEditText>(R.id.p_name).text =Editable.Factory.getInstance().newEditable(items[position].title)
//                view.findViewById<TextInputEditText>(R.id.p_qty).text =Editable.Factory.getInstance().newEditable(items[position].quantity)
//
//
//                view.findViewById<Button>(R.id.submit_btn).setOnClickListener {
//                    Log.d("hello", "itemClickListener: ${view.findViewById<TextInputEditText>(R.id.p_price).text} ")
//                    dialog.dismiss()
//                }
//
//                dialog.show()
//            }
//        })
    }
}