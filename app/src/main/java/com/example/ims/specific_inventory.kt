package com.example.ims

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.sourceInformationMarkerEnd
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ims.databinding.FragmentSpecificInventoryBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class specific_inventory : Fragment() {
    lateinit var previewDialog: BottomSheetDialog
    lateinit var binding: FragmentSpecificInventoryBinding
    lateinit var product: ArrayList<product_list>
    lateinit var inventory_id: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpecificInventoryBinding.inflate(inflater, container, false)

        // Retrieve the data from the arguments
        val inv_id = arguments?.getString("hello")
        inventory_id = inv_id.toString()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.rvInvProduct
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        previewDialog = BottomSheetDialog(requireContext())
        product = arrayListOf()
        val data = product_list("Product 1", "Inventory 1", "4")
        product.add(data)
        product.add(data)
        product.add(data)
        product.add(data)

        load_data()
        sharedPreferences =
            requireContext().getSharedPreferences("USERDATA", AppCompatActivity.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        val access_token = sharedPreferences.getString("ACCESS_TOKEN", "None")

        Retro_setup.apiService.getInventoryItems("04",access_token!!)
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    val a = response.body()
                    Log.d("API_CHECK", "onResponse: ${response.code()}")
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.d("API_CHECK", "onFailure: ${t.message}")
                }

            })
    }

    fun load_data() {
        val specific_inv_adapter = specific_inv_adapter(product)
        binding.rvInvProduct.adapter = specific_inv_adapter

        specific_inv_adapter.onItem(object : specific_inv_adapter.onitemclick {
            override fun itemClickListener(position: Int) {
                val view =
                    View.inflate(requireContext(), R.layout.preview_dialog, null)
                previewDialog.setContentView(view)
                previewDialog.show()
                previewDialog.setCancelable(true)
                previewDialog.setCanceledOnTouchOutside(true)

                view.findViewById<TextView>(R.id.product_name).text = product[position].title
                view.findViewById<TextView>(R.id.product_unit).text =
                    product[position].quantity_left
                view.findViewById<TextView>(R.id.inv_name).text = product[position].inventory_name

            }

        })
    }

}