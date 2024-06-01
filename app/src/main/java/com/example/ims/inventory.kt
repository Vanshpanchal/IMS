package com.example.ims

import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.ims.databinding.FragmentInventoryBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import retrofit2.Call
//import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.math.log

class inventory : Fragment() {
    lateinit var binding: FragmentInventoryBinding
    lateinit var inventory: ArrayList<inventory_list>
    lateinit var inv_data: ArrayList<DataX>
    lateinit var previewDialog: BottomSheetDialog
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
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
        inv_data = arrayListOf()

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

        binding.addInv.setOnClickListener {
            val view = LayoutInflater.from(requireContext())
                .inflate(R.layout.inventory_dialog, null, false)
            val dialog = MaterialAlertDialogBuilder(
                requireContext()
            )
                .setView(view)
                .create()

            view.findViewById<Button>(R.id.submit_btn).setOnClickListener {
                Log.d("hello", "itemClickListener: $} ")
                dialog.dismiss()

            }
            dialog.show()
        }

        sharedPreferences =
            requireContext().getSharedPreferences("USERDATA", AppCompatActivity.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        inv_api()

    }

    private fun inv_api() {
        val u_id = sharedPreferences.getString("U_ID", "None")
        val access_token = sharedPreferences.getString("ACCESS_TOKEN", "None")
        Log.d("API_CHECK", "onViewCreated: $u_id ")

        val url = "http://10.0.2.2:8000/inventory/show"

        val getRequest = CustomGetReq(
            url,
            access_token!!,
            { response ->
                val jsonResponse = JSONObject(response)
                val data = jsonResponse.getJSONArray("data").toString()


                val gson = Gson()
                val ListType = object : TypeToken<ArrayList<DataX>>() {}.type
                val users: ArrayList<DataX> = gson.fromJson(data, ListType)
                for (i in users) {
                    inv_data.add(i)
                }
                Log.d("API_CHECK", "Response: $users")
                load_data(inv_data)
            },
            { error ->
                Log.d("API_CHECK", "Error: ${String(error.networkResponse.data)}")
            }
        )
        Volley.newRequestQueue(requireContext()).add(getRequest)


//        Retro_setup.apiService.getInventory(access_token!!)
//            .enqueue(object : retrofit2.Callback<m_inventory> {
//                override fun onResponse(call: Call<m_inventory>, response: Response<m_inventory>) {
//                    val res = response.body()?.data
//                    for (i in res!!) {
//                        inv_data.add(i)
//                    }
//                    Log.d("API_CHECK", "onResponse: ${res}")
//                    Log.d("API_CHECK", "onResponse++: ${inv_data}")
//
//                    load_data(inv_data)
////                Log.d("API_CHECK", "onResponse: ${}")
//                }
//
//                override fun onFailure(call: Call<m_inventory>, t: Throwable) {
//                    Log.d("API_CHECK", "onFailure: ${t.message}")
//                }
//
//            })
    }

    private fun load_data(inv: ArrayList<DataX>) {
        var inventoryAdapter = inventory_adapter(inv)
        binding.rvInventory.adapter = inventoryAdapter
        Log.d("API_CHECK", "load_data: $inv")
        inventoryAdapter.onItem(object : inventory_adapter.onitemclick {
            override fun itemClickListener(position: Int) {
                Log.d("hello", "itemClickListener: ${inv[position].inventoryId}")

                val frag = specific_inventory()
                val bundle = Bundle()
                bundle.putString("inv_id", "${inv[position].inventoryId}")
                bundle.putString("inv_name", "${inv[position].inventoryName}")
                frag.arguments = bundle
                (activity as? MainActivity)?.replacefragement(frag, "specific_inventory")
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

                view.findViewById<ImageView>(R.id.img)


                view.findViewById<TextView>(R.id.inv_name).text = inv_data[position].inventoryName
                view.findViewById<TextView>(R.id.inv_owner).text = inv_data[position].ManagerName
                view.findViewById<TextView>(R.id.created_date).text =
                    inv_data[position].createdAt
                view.findViewById<TextView>(R.id.inv_country).text =
                    inv_data[position].country
                view.findViewById<TextView>(R.id.inv_mobile).text =
                    inv_data[position].mobileNo
                view.findViewById<TextView>(R.id.inv_address).text =
                    inv_data[position].address
                view.findViewById<TextView>(R.id.inv_category).text =
                    inv_data[position].category
                view.findViewById<TextView>(R.id.inv_id).text = inv_data[position].inventoryId
                return true
            }

        })
    }
}
