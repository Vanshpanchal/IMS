package com.example.ims

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ims.databinding.FragmentDashboardBinding
import com.example.ims.databinding.FragmentProductBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
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
            product_list("Product 1", "4500/-"),
            product_list("Product 2", "2000/-"),
            product_list("Product 3", "1200/-"),
            product_list("Product 4", "1000000/-"),
            product_list("Product 5", "90000000/-"),
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

            }

        })

        productAdapter.ondelete(object : product_adapter.onitemclick {
            override fun itemClickListener(position: Int) {
                Log.d("hello", "delete itemClickListener: ${position}")

            }

        })
    }
}