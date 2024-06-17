package com.example.ims

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.ims.databinding.FragmentInventoryBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

//import retrofit2.Response

class inventory : Fragment() {
    lateinit var binding: FragmentInventoryBinding
    lateinit var inventory: ArrayList<inventory_list>
    lateinit var inventoryItems: ArrayList<InventoryItems>
    lateinit var inv_data: ArrayList<DataX>
    lateinit var previewDialog: BottomSheetDialog
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var auth: FirebaseAuth
    private lateinit var fs: FirebaseFirestore
    private var P_longitude = 0.0
    private var P_latitude = 0.0
    private var isAdmin = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inventory = arrayListOf()
        inv_data = arrayListOf()
        inventoryItems = arrayListOf()
        auth = FirebaseAuth.getInstance()
        fs = FirebaseFirestore.getInstance()
//        val items = arrayOf("Item 1", "Item 2", "Item 3", "Item 4")
//        (binding.textField.editText as? MaterialAutoCompleteTextView)?.setSimpleItems(items)
//        getInventory()
//        addressApi("Vadodara")
        checkUser()

        fs.collection("Users").document(auth.currentUser?.uid!!).get().addOnSuccessListener {
            isAdmin = it.get("Admin") as Boolean
            if (isAdmin) {
                binding.addInv.visibility = View.GONE
            }
        }
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

            fs.collection("Users").document(auth.currentUser?.uid!!).get().addOnSuccessListener {
                view.findViewById<TextInputEditText>(R.id.i_owner).text =
                    Editable.Factory.getInstance()
                        .newEditable(it.get("Uname").toString())
            }

            view.findViewById<ImageButton>(R.id.locate).visibility = View.GONE
//            view.findViewById<ImageButton>(R.id.locate).setOnClickListener {
//                val address = view.findViewById<TextInputEditText>(R.id.address).text.toString()
//                addressApi(address)
//            }

            view.findViewById<Button>(R.id.submit_btn).setOnClickListener {

                if (view.findViewById<TextInputEditText>(R.id.i_name).text.toString()
                        .isNotEmpty() && view.findViewById<TextInputEditText>(R.id.country).text.toString()
                        .isNotEmpty() && view.findViewById<TextInputEditText>(R.id.mobile_no).text.toString()
                        .isNotEmpty() && view.findViewById<TextInputEditText>(R.id.address).text.toString()
                        .isNotEmpty() && view.findViewById<TextInputEditText>(R.id.i_owner).text.toString()
                        .isNotEmpty()
                ) {


                    val inventoryInfo = hashMapOf(
                        "InventoryName" to view.findViewById<TextInputEditText>(R.id.i_name).text.toString(),
                        "Country" to view.findViewById<TextInputEditText>(R.id.country).text.toString(),
                        "MobileNo" to view.findViewById<TextInputEditText>(R.id.mobile_no).text.toString(),
                        "Address" to view.findViewById<TextInputEditText>(R.id.address).text.toString(),
                        "InventoryOwner" to view.findViewById<TextInputEditText>(R.id.i_owner).text.toString(),
                        "CreatedAt" to Timestamp.now().toDate(),
                        "UserID" to auth.currentUser?.uid!!

                    )
                    fs.collection("Inventory").document(auth.currentUser?.uid!!)
                        .collection("MyInventory").document().set(inventoryInfo)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                custom_snackbar("Inventory Added")
                                checkUser()
//                                getInventory()

                            } else {
                                custom_snackbar("Error")
                            }
                        }
                    Log.d("hello", "itemClickListener: $} ")
                    dialog.dismiss()
                } else {
                    custom_snackbar("Enter Proper Credentials")
                }
            }
            dialog.show()
        }


        sharedPreferences =
            requireContext().getSharedPreferences("USERDATA", AppCompatActivity.MODE_PRIVATE)
        editor = sharedPreferences.edit()

//        load_data(inv_data)
//        inv_api()

    }

    private fun addressApi(Address: String, Uid: String) {
        var cordinates = listOf<Double>()
        val url =
            "https://api.geoapify.com/v1/geocode/search?text=$Address&apiKey=a4df04f3e2154cafbf08d57831558743"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->

                var longitude = 0.0
                var latitude = 0.0

                val jsonObject = JSONObject(response)
                val featuresArray = jsonObject.getJSONArray("features")

//                val latitudes = mutableListOf<Double>()
//                val longitudes = mutableListOf<Double>()


//                for (i in 0 until featuresArray.length()) {
                val feature = featuresArray.getJSONObject(0)
                val geometry = feature.getJSONObject("geometry")
                val coordinates = geometry.getJSONArray("coordinates")
                if (coordinates.length() >= 2) {
                    longitude = coordinates.getDouble(0)
                    latitude = coordinates.getDouble(1)
                    P_longitude = longitude
                    P_latitude = latitude
//                    }
                }
                Log.d("D_CHECK", "addressApi: ${longitude}  $latitude}")
                fs = FirebaseFirestore.getInstance()
                val cordinates = hashMapOf(
                    "Longitude" to P_longitude,
                    "Latitude" to P_latitude,
                    "Address" to Address,
                    "CreatedAt" to Timestamp.now().toDate(),
                    "UserID" to Uid
                )
                fs.collection("Cordinates").document(auth.currentUser?.uid!!)
                    .collection("MyCordinates").document().set(
                        cordinates
                    ).addOnSuccessListener {
                        Log.d(
                            "D_CHECK",
                            "Successfully added to firestore addressApi: ${cordinates}"
                        )
                    }
// Cordinates
//                Log.d("D_CHECK", "addressApi: ${cordinates}")
            },
            { error ->
                // Handle error
            })
        Volley.newRequestQueue(requireContext()).add(stringRequest)


    }

    private fun simulateDataLoading() {
        binding.ProgressBar.postDelayed({
            binding.rvInventory.visibility = View.VISIBLE

        }, 2000)
    }

    private fun getInventory() {
        fs.collection("Inventory").document(auth.currentUser?.uid!!).collection("MyInventory")
            .orderBy("CreatedAt", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { it ->
                val inventoryItemList = ArrayList<InventoryItems>()
                inventoryItems.clear()
                for (data in it) {

                    val r = data.toObject(InventoryItems::class.java)
                    r.InventoryID = data.id
                    Log.d("D_CHECK", "getInventory: $r")
                    inventoryItems.add(r)
                }
                Log.d("D_CHECK", "getInventory: $inventoryItems")
                updateUi(inventoryItems)
            }
    }

    private fun s_getinventory() {
        inventoryItems.clear()
        fs.collection("Users").get().addOnSuccessListener {
            for (data in it) {
                Log.d("D_CHECK", "s_getinventory: ${data.id}")
                fs.collection("Inventory").document(data.id).collection("MyInventory")
                    .orderBy("CreatedAt", Query.Direction.ASCENDING)
                    .get()
                    .addOnSuccessListener { it ->
                        val inventoryItemList = ArrayList<InventoryItems>()
                        for (data in it) {

                            val r = data.toObject(InventoryItems::class.java)
                            r.InventoryID = data.id
                            Log.d("D_CHECK", "getInventory: $r")
                            inventoryItems.add(r)
                        }
                        updateUi(inventoryItems)
                        Log.d("D_CHECK", "getInventory: $inventoryItems")
                    }
            }
        }

    }

    private fun custom_snackbar(message: String) {
        val bar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
        bar.setBackgroundTint(resources.getColor(R.color.blue))
        bar.setAction("OK") {
            bar.dismiss()
        }
        bar.setActionTextColor(resources.getColor(R.color.blue3))
        bar.show()
    }

    private fun updateUi(inv: ArrayList<InventoryItems>) {
        var inventoryAdapter = inventory_adapter(inv)
        binding.rvInventory.adapter = inventoryAdapter
        binding.rvInventory.viewTreeObserver.addOnGlobalLayoutListener {
            binding.ProgressBar.visibility = View.GONE
        }
        simulateDataLoading()
        Log.d("D_CHECK", "load_data: $inv")
        inventoryAdapter.onItem(object : inventory_adapter.onitemclick {
            override fun itemClickListener(position: Int) {
//                Log.d("hello", "itemClickListener: ${inv[position].InventoryOwner}")

                val frag = specific_inventory()
                val bundle = Bundle()
                bundle.putString("inv_name", "${inv[position].InventoryName}")
                bundle.putString("inv_id", "${inv[position].InventoryID}")
                bundle.putString("uid", "${inv[position].UserID}")
                Log.d("D_CHECK", "itemClickListener---: ${inv[position].UserID}")
                frag.arguments = bundle
                (activity as? MainActivity)?.replacefragement(frag, "specific_inventory")
            }

        })

        inventoryAdapter.onItem_1(object : inventory_adapter.OnItemLongClickListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onItemLongClick(position: Int): Boolean {
                Log.d("HELLO", "onItemLongClick: ${position}")
                val view =
                    View.inflate(requireContext(), R.layout.preview_dialog_2, null)
                previewDialog.setContentView(view)
                previewDialog.show()
                previewDialog.setCancelable(true)
                previewDialog.setCanceledOnTouchOutside(true)

                fs.collection("Users").document(auth.currentUser?.uid!!).get()
                    .addOnSuccessListener {
                        isAdmin = it.get("Admin") as Boolean
                        if (isAdmin) {
                            previewDialog.findViewById<Button>(R.id.delete)?.visibility = View.GONE
                        }
                    }
                view.findViewById<Button>(R.id.delete).setOnClickListener {
                    MaterialAlertDialogBuilder(requireContext()).setTitle("Inventory Delete")
                        .setMessage("Are You Sure You Want To Delete ${inv[position].InventoryName} Inventory")
                        .setIcon(R.drawable.delete_forever_24px)
                        .setPositiveButton("Yes") { _, _ ->
                            fs.collection("Inventory").document(auth.currentUser?.uid!!)
                                .collection("MyInventory")
                                .document(inv[position].InventoryID.toString())
                                .delete().addOnSuccessListener {
                                    fs.collection("Product").document(auth.currentUser?.uid!!)
                                        .collection("MyProduct").whereEqualTo("InventoryId", inv[position].InventoryID).get().addOnSuccessListener {
                                            for (document in it.documents) {
                                               document.reference.delete().addOnSuccessListener {
                                                   Log.d("D_CHECK", "onItemLongClick: Product Deleted from Product")
                                               }
                                            }
                                        }
                                    Log.d("D_CHECK", "onItemLongClick: Inventory Deleted")
                                    custom_snackbar("Inventory Deleted")
//                                    getInventory()
                                    checkUser()
                                }
                        }
                        .setNegativeButton("No") { _, _ ->
                            previewDialog.dismiss()
                        }
                        .show()
                    previewDialog.dismiss()
                }

                view.findViewById<Button>(R.id.locate).setOnClickListener {
                    val address = view.findViewById<TextView>(R.id.inv_address).text.toString()
                    addressApi(address, inv[position].UserID!!)
                    val mapFragment = MapsFragment()
                    (activity as? MainActivity)?.replacefragement(mapFragment, "specific_inventory")
                    previewDialog.dismiss()
                }
                val date = inv[position].CreatedAt

                val obj = date?.toDate()
                val timeZone = ZoneId.of("UTC")
                val localDateTime =
                    LocalDateTime.ofInstant(Instant.ofEpochMilli(obj?.time!!), timeZone)
                val year = localDateTime.year.toString()
                val month = localDateTime.month.getDisplayName(
                    TextStyle.SHORT,
                    Locale.getDefault()
                ).toString()
                val day = localDateTime.dayOfMonth.toString()

                val dateString = "$day $month $year"

                view.findViewById<ImageView>(R.id.img)


                view.findViewById<TextView>(R.id.inv_name).text =
                    inv[position].InventoryName
                view.findViewById<TextView>(R.id.inv_owner).text =
                    inv[position].InventoryOwner
                view.findViewById<TextView>(R.id.created_date).text =
                    dateString
                view.findViewById<TextView>(R.id.inv_country).text =
                    inv[position].Country
                view.findViewById<TextView>(R.id.inv_mobile).text =
                    inv[position].MobileNo
                view.findViewById<TextView>(R.id.inv_address).text =
                    inv[position].Address
                view.findViewById<TextView>(R.id.inv_id).text = inv[position].InventoryID
                return true
            }

        })
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
//                load_data(inv_data)
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

//    private fun load_data(inv: ArrayList<DataX>) {
//        var inventoryAdapter = inventory_adapter(inv)
//        binding.rvInventory.adapter = inventoryAdapter
//        Log.d("API_CHECK", "load_data: $inv")
//        inventoryAdapter.onItem(object : inventory_adapter.onitemclick {
//            override fun itemClickListener(position: Int) {
//                Log.d("hello", "itemClickListener: ${inv[position].inventoryId}")
//
//                val frag = specific_inventory()
//                val bundle = Bundle()
//                bundle.putString("inv_id", "${inv[position].inventoryId}")
//                bundle.putString("inv_name", "${inv[position].inventoryName}")
//                frag.arguments = bundle
//                (activity as? MainActivity)?.replacefragement(frag, "specific_inventory")
//            }
//
//        })
//
//        inventoryAdapter.onItem_1(object : inventory_adapter.OnItemLongClickListener {
//            override fun onItemLongClick(position: Int): Boolean {
//                Log.d("HELLO", "onItemLongClick: ${position}")
//                val view =
//                    View.inflate(requireContext(), R.layout.preview_dialog_2, null)
//                previewDialog.setContentView(view)
//                previewDialog.show()
//                previewDialog.setCancelable(true)
//                previewDialog.setCanceledOnTouchOutside(true)
//
//                view.findViewById<ImageView>(R.id.img)
//
//
//                view.findViewById<TextView>(R.id.inv_name).text =
//                    inv_data[position].inventoryName
//                view.findViewById<TextView>(R.id.inv_owner).text =
//                    inv_data[position].ManagerName
//                view.findViewById<TextView>(R.id.created_date).text =
//                    inv_data[position].createdAt
//                view.findViewById<TextView>(R.id.inv_country).text =
//                    inv_data[position].country
//                view.findViewById<TextView>(R.id.inv_mobile).text =
//                    inv_data[position].mobileNo
//                view.findViewById<TextView>(R.id.inv_address).text =
//                    inv_data[position].address
//                view.findViewById<TextView>(R.id.inv_category).text =
//                    inv_data[position].category
//                view.findViewById<TextView>(R.id.inv_id).text = inv_data[position].inventoryId
//                return true
//            }
//
//        })
//    }
//

    private fun checkUser() {
        fs.collection("Users").document(auth.currentUser?.uid!!).get().addOnSuccessListener {
            isAdmin = it.get("Admin") as Boolean
            if (isAdmin) {
                s_getinventory()
            } else {
                getInventory()
            }
        }
    }

}
