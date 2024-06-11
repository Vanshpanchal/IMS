package com.example.ims

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.ims.databinding.CustomProgressBinding
import com.example.ims.databinding.FragmentSpecificInventoryBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

//import retrofit2.Response

class specific_inventory : Fragment() {

    lateinit var previewDialog: BottomSheetDialog
    lateinit var binding: FragmentSpecificInventoryBinding
    lateinit var product: ArrayList<inv_itemsItem>
    lateinit var inventory_id: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var pendingIntent: PendingIntent
    private lateinit var sharedPreferences_1: SharedPreferences
    private lateinit var editor_1: SharedPreferences.Editor
    private lateinit var sr: StorageReference
    private lateinit var auth: FirebaseAuth
    private lateinit var fs: FirebaseFirestore
    private lateinit var add_dailog: View

    var imageUri: Uri = android.net.Uri.EMPTY
    private var product_img: ImageView? = null

    private var galleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
//                    galleryprofile()
                val dialog = Dialog(requireContext())
                val layout = CustomProgressBinding.inflate(layoutInflater)
                dialog.setContentView(layout.root)
                dialog.setCanceledOnTouchOutside(false)
                dialog.show()
                imageUri = result?.data!!.data!!
                product_img?.setImageURI(result.data!!.data)
                sr = FirebaseStorage.getInstance()
                    .getReference("Product/" + auth.currentUser?.uid)
                    .child("Inv${inventory_id}_Product${result.data?.data?.lastPathSegment}")
                sr.putFile(result.data?.data!!).addOnSuccessListener {
                    Log.d("D_CHECK", "Product Image Uploaded ")
                    dialog.dismiss()

                }.addOnFailureListener {
                    Log.d("D_CHECK", "Product Image Not Uploaded ")
                    dialog.dismiss()
                }

//                imageUri = result.data?.data
//                LoadImg(add_dailog, result.data?.data!!)


//                product_img?.setImageURI(result.data?.data)
            }
        }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentSpecificInventoryBinding.inflate(inflater, container, false)

        // Retrieve the data from the arguments
        val inv_name = arguments?.getString("inv_name")
        val inv_id = arguments?.getString("inv_id")
        inventory_id = inv_id!!
        binding.textView.text = inv_name
        auth = FirebaseAuth.getInstance()
        fs = FirebaseFirestore.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.rvInvProduct
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        previewDialog = BottomSheetDialog(requireContext())
        product = arrayListOf()
        sharedPreferences =
            requireContext().getSharedPreferences("USERDATA", AppCompatActivity.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        sharedPreferences_1 =
            requireContext().getSharedPreferences("FOOD", AppCompatActivity.MODE_PRIVATE)
        editor_1 = sharedPreferences_1.edit()
        get_data()
//        if (product.size != 0) {
//            monitorStock()
//        }
//        for (i in 1..5) {
//            showNotification("Hello${1}", "World")
//        }


        binding.toggleButton.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.Category -> sort_data(1)
                    R.id.Time -> sort_data(2)
                    R.id.Name -> sort_data(3)
                    R.id.Stock -> sort_data(4)
                    else -> get_data()
                }
            } else {
                get_data()
            }
        }
        binding.addProduct.setOnClickListener {
            add_dailog = LayoutInflater.from(requireContext())
                .inflate(R.layout.product_add_dialog, null, false)
            val dialog = MaterialAlertDialogBuilder(
                requireContext()
            ).setView(add_dailog).create()
            product_img = add_dailog.findViewById<ImageView>(R.id.P_img)
            add_dailog.findViewById<ImageView>(R.id.P_img).setOnClickListener {
                requestpermission()
            }
            add_dailog.findViewById<Button>(R.id.submit_btn).setOnClickListener {
                Log.d("TAG", "result:${imageUri} ")
                val product = hashMapOf(
                    "ItemName" to add_dailog.findViewById<TextView>(R.id.product_name).text.toString(),
                    "PricePerUnit" to add_dailog.findViewById<TextView>(R.id.priceper_unit).text.toString(),
                    "Stock" to add_dailog.findViewById<TextView>(R.id.stock).text.toString(),
                    "InventoryId" to inventory_id,
                    "ProductId" to imageUri.lastPathSegment.toString(),
                    "Category" to add_dailog.findViewById<TextView>(R.id.P_category).text.toString(),
                    "CreatedAt" to Timestamp.now().toDate(),
                    "LowStock" to "-1"
                )


                fs.collection("Product").document(auth.currentUser?.uid!!).collection("MyProduct")
                    .document().set(product).addOnSuccessListener {
                        custom_snackbar("Product Added")
                        get_data()
                    }.addOnFailureListener {
                        Log.d("D_CHECK", "onViewCreated: ${it.message}")
                    }






                dialog.dismiss()

            }
            dialog.show()
        }
    }


    fun LoadImg(view: View, imguri: Uri?) {
        Glide.with(this).load(imguri).listener(object : RequestListener<Drawable?> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable?>?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable?>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

        }).into(view.findViewById(R.id.P_img))
    }

    public fun get_data() {
        Log.d("D_CHECK", "getInventory: $inventory_id")

        fs = FirebaseFirestore.getInstance()
        fs.collection("Product").document(auth.currentUser?.uid!!).collection("MyProduct")
            .whereEqualTo("InventoryId", inventory_id).get().addOnSuccessListener {
                product.clear()
                for (data in it) {
                    val r = data.toObject(inv_itemsItem::class.java)
                    Log.d("D_CHECK", "getInventory: $r")
                    product.add(r)
                }
                product.sortBy { it.CreatedAt }
                load_data(product)
            }
    }

    private fun simulateDataLoading() {
        binding.ProgressBar.postDelayed({
            binding.rvInvProduct.visibility = View.VISIBLE

        }, 2000)
    }

    fun load_data(product: ArrayList<inv_itemsItem>) {
        simulateDataLoading()
        val specific_inv_adapter = specific_inv_adapter(product)
        binding.rvInvProduct.adapter = specific_inv_adapter
        binding.rvInvProduct.viewTreeObserver.addOnGlobalLayoutListener {
            binding.ProgressBar.visibility = View.GONE
        }
        specific_inv_adapter.onItem(object : specific_inv_adapter.onitemclick {
            override fun itemClickListener(position: Int) {
                sr = FirebaseStorage.getInstance()
                    .getReference("Product/" + auth.currentUser?.uid)
                    .child("Inv${inventory_id}_Product${product[position].ProductId}")
                val view = View.inflate(requireContext(), R.layout.preview_dialog, null)
                val imageView = view.findViewById<ImageView>(R.id.P_img)
                sr.downloadUrl.addOnSuccessListener {
                    Glide.with(requireContext()).load(it).into(imageView)
                }
                previewDialog.setContentView(view)
                previewDialog.show()
                previewDialog.setCancelable(true)
                previewDialog.setCanceledOnTouchOutside(true)
                if (product[position].LowStock?.toInt() != -1) {
                    view.findViewById<Button>(R.id.remove_notify).visibility = View.VISIBLE
                }
                view.findViewById<Button>(R.id.remove_notify).setOnClickListener {
                    fs.collection("Product").document(auth.currentUser?.uid!!)
                        .collection("MyProduct")
                        .whereEqualTo("ProductId", product[position].ProductId).get()
                        .addOnSuccessListener {
                            for (doc in it) {
                                val docRef = doc.reference

                                docRef.update(
                                    "LowStock",
                                    "-1"
                                ).addOnSuccessListener {
                                    get_data()
                                    custom_snackbar("Stock Alert For ${product[position].ItemName} Updated Successfully")
                                    monitorStock()
                                    Log.d("D_CHECK", "onItemLongClick: Updated")
                                    previewDialog.dismiss()
                                }.addOnFailureListener {
                                    Log.d("D_CHECK", "onItemLongClick: ${it.message}")
                                }
                            }
                            Log.d("D_CHECK", "onItemLongClick: ${it}")
                        }.addOnFailureListener {
                            Log.d("D_CHECK", "onItemLongClick: ${it.message}")
                        }
                }
                val notifyBtn = view.findViewById<Button>(R.id.notify)
                val notifyview =
                    View.inflate(requireContext(), R.layout.edit_dialog, null)
                notifyBtn.setOnClickListener {
                    MaterialAlertDialogBuilder(requireContext()).apply {
                        setView(notifyview)
                        notifyview.findViewById<TextView>(R.id.tvmsg).text = "Notify Low Stock"
                        notifyview.findViewById<TextView>(R.id.textView).text =
                            "Stock Alert For ${product[position].ItemName}"
                        notifyview.findViewById<TextInputLayout>(R.id.layout_3).hint = "Alert Stock"
                        notifyview.findViewById<TextInputEditText>(R.id.p_qty).text =
                            Editable.Factory.getInstance().newEditable(product[position].LowStock)
                        previewDialog.dismiss()
                        setPositiveButton("Save") { dialog, which ->
                            fs.collection("Product").document(auth.currentUser?.uid!!)
                                .collection("MyProduct")
                                .whereEqualTo("ProductId", product[position].ProductId).get()
                                .addOnSuccessListener {
                                    for (doc in it) {
                                        val docRef = doc.reference
                                        val qty =
                                            notifyview.findViewById<TextInputEditText>(R.id.p_qty).text.toString()
                                                .toInt()
                                        docRef.update(
                                            "LowStock",
                                            notifyview.findViewById<TextInputEditText>(R.id.p_qty).text.toString()
                                        ).addOnSuccessListener {
                                            get_data()
                                            custom_snackbar("Stock Alert For ${product[position].ItemName} Updated Successfully")
                                            monitorStock()
                                            Log.d("D_CHECK", "onItemLongClick: Updated")
                                        }.addOnFailureListener {
                                            Log.d("D_CHECK", "onItemLongClick: ${it.message}")
                                        }
                                    }
                                    Log.d("D_CHECK", "onItemLongClick: ${it}")
                                }.addOnFailureListener {
                                    Log.d("D_CHECK", "onItemLongClick: ${it.message}")
                                }
                            dialog.dismiss()
                        }
                    }.show()
                }

                val deleteBtn = view.findViewById<Button>(R.id.delete)
                deleteBtn.setOnClickListener {
                    MaterialAlertDialogBuilder(
                        requireContext(),
                    )
                        .setTitle("Remove Product")
                        .setIcon(R.drawable.product)
                        .setMessage("Are you sure you want to remove ${product[position].ItemName}?")
                        .setPositiveButton("Yes") { dialog, which ->
                            sr = FirebaseStorage.getInstance()
                                .getReference("Product/" + auth.currentUser?.uid)
                                .child("Inv${inventory_id}_Product${product[position].ProductId}")
                            sr.delete()

//                        val product_ID = product[position]
                            fs.collection("Product").document(auth.currentUser?.uid!!)
                                .collection("MyProduct")
                                .whereEqualTo("ProductId", product[position].ProductId).get()
                                .addOnSuccessListener {
                                    for (doc in it) {
                                        val docRef = doc.reference
                                        docRef.delete().addOnSuccessListener {
                                            get_data()
                                            Log.d("D_CHECK", "onItemLongClick: Deleted")
                                        }.addOnFailureListener {
                                            Log.d("D_CHECK", "onItemLongClick: ${it.message}")
                                        }
                                    }
                                    Log.d("D_CHECK", "onItemLongClick: ${it}")
                                }.addOnFailureListener {
                                    Log.d("D_CHECK", "onItemLongClick: ${it.message}")
                                }

                            dialog.dismiss()
                        }
                        .setNegativeButton("No") { dialog, which ->
                            dialog.dismiss()
                        }
                        .show();
                }
                val imageUrl = "----"
                view.findViewById<TextView>(R.id.product_name).text = product[position].ItemName
                view.findViewById<TextView>(R.id.product_unit).text =
                    product[position].Stock.toString()
                view.findViewById<TextView>(R.id.category).text = product[position].Category
                view.findViewById<TextView>(R.id.pp_unit).text =
                    product[position].PricePerUnit.toString() + "/-"
                view.findViewById<TextView>(R.id.P_id).text = product[position].ProductId
            }

        })

        specific_inv_adapter.onItem_1(object : specific_inv_adapter.OnItemLongClickListener {
            override fun onItemLongClick(position: Int): Boolean {
                MaterialAlertDialogBuilder(
                    requireContext(),
                )
                    .setTitle("Remove Product")
                    .setIcon(R.drawable.product)
                    .setMessage("Are you sure you want to remove ${product[position].ItemName}?")
                    .setPositiveButton("Yes") { dialog, which ->
                        sr = FirebaseStorage.getInstance()
                            .getReference("Product/" + auth.currentUser?.uid)
                            .child("Inv${inventory_id}_Product${product[position].ProductId}")
                        sr.delete()

//                        val product_ID = product[position]
                        fs.collection("Product").document(auth.currentUser?.uid!!)
                            .collection("MyProduct")
                            .whereEqualTo("ProductId", product[position].ProductId).get()
                            .addOnSuccessListener {
                                for (doc in it) {
                                    val docRef = doc.reference
                                    docRef.delete().addOnSuccessListener {
                                        get_data()
                                        Log.d("D_CHECK", "onItemLongClick: Deleted")
                                    }.addOnFailureListener {
                                        Log.d("D_CHECK", "onItemLongClick: ${it.message}")
                                    }
                                }
                                Log.d("D_CHECK", "onItemLongClick: ${it}")
                            }.addOnFailureListener {
                                Log.d("D_CHECK", "onItemLongClick: ${it.message}")
                            }

                        dialog.dismiss()
                    }
                    .setNegativeButton("No") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show();
                return true
            }

        })

        specific_inv_adapter.onEdit(object : specific_inv_adapter.EditClick {
            override fun onEditClick(position: Int) {
                Log.d("D_CHECK", "onEditClick: $position")
                val view =
                    View.inflate(requireContext(), R.layout.edit_dialog, null)

                view.findViewById<TextView>(R.id.textView).text = product[position].ItemName
                view.findViewById<TextInputEditText>(R.id.p_qty).text =
                    Editable.Factory.getInstance().newEditable(product[position].Stock)
                MaterialAlertDialogBuilder(requireContext()).apply {
                    setView(view)
                    setPositiveButton("Save") { dialog, which ->
                        fs.collection("Product").document(auth.currentUser?.uid!!)
                            .collection("MyProduct")
                            .whereEqualTo("ProductId", product[position].ProductId).get()
                            .addOnSuccessListener {
                                for (doc in it) {
                                    val docRef = doc.reference
                                    docRef.update(
                                        "Stock",
                                        view.findViewById<TextInputEditText>(R.id.p_qty).text.toString()
                                    ).addOnSuccessListener {
                                        get_data()
                                        custom_snackbar("${product[position].ItemName} Updated Successfully")
                                        monitorStock()
                                        Log.d("D_CHECK", "onItemLongClick: Updated")
                                    }.addOnFailureListener {
                                        Log.d("D_CHECK", "onItemLongClick: ${it.message}")
                                    }
                                }
                                Log.d("D_CHECK", "onItemLongClick: ${it}")
                            }.addOnFailureListener {
                                Log.d("D_CHECK", "onItemLongClick: ${it.message}")
                            }
                        dialog.dismiss()
                    }
                }.show()
            }

        })
    }

    private fun sort_data(sortDetails: Int) {

        fs = FirebaseFirestore.getInstance()
        fs.collection("Product").document(auth.currentUser?.uid!!).collection("MyProduct")
            .whereEqualTo("InventoryId", inventory_id).get().addOnSuccessListener {

                product.clear()
                for (data in it) {
                    val r = data.toObject(inv_itemsItem::class.java)
                    product.add(r)
                }
                when (sortDetails) {
                    1 -> {
                        product.sortBy { it.Category }
                    }

                    2 -> {
                        product.sortBy { it.CreatedAt }
                    }

                    3 -> {
                        product.sortBy { it.ItemName }
                    }

                    4 -> {
                        product.sortBy { it.Stock }
                    }
                }
                load_data(product)

            }
    }

    private fun checkpermissionRead() = ActivityCompat.checkSelfPermission(
        requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE
    ) == PackageManager.PERMISSION_GRANTED

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkpermissionReadImages() = ActivityCompat.checkSelfPermission(
        requireContext(), android.Manifest.permission.READ_MEDIA_IMAGES
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestpermission() {
        val permissiontoRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= 33) {
            if (!checkpermissionReadImages()) {
                permissiontoRequest.add(android.Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galleryLauncher.launch(galleryIntent)
            }

            if (permissiontoRequest.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    requireContext() as Activity, permissiontoRequest.toTypedArray(), 0
                )
            }
        } else {
            if (!checkpermissionRead()) {
                permissiontoRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                galleryLauncher.launch(galleryIntent)
////                profileImage()
//                profile()
            }

            if (permissiontoRequest.isNotEmpty()) {
                ActivityCompat.requestPermissions(
                    requireContext() as Activity, permissiontoRequest.toTypedArray(), 0
                )
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && grantResults.isNotEmpty()) {
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("hello", "onRequestPermissionsResult: Done")

                }
            }
        }
    }

    fun add_product(inv_id: String, access_token: String, imageView: Uri) {
        val url = "http://10.0.2.2:8000/items/add"
        Log.d("hello", "itemClickListener: ${imageView}} ")
//                val img = drawableToByteArray(R.drawable.img2)
        val bodyParams = JSONObject().apply {
            put("itemName", "Shoes")
            put("itemId", "itm01")
            put("pricePerUnit", "100")
            put("stock", "10")
            put("inventoryId", "05")
            put("category", "Shoes")
            put("itemimage", imageView)
        }
        val params = mapOf(
            "inventoryId___" to inv_id

        )
        val getRequest = CustomReq1(url, access_token!!, params, bodyParams, { response ->

            Log.d("API_CHECK", "Response: ${response}")

        }, { error ->
            Log.d("API_CHECK", "Error: ${String(error.networkResponse.data)}")
        })

        Volley.newRequestQueue(requireContext()).add(getRequest)
    }

    fun fetch_api() {
        val access_token = sharedPreferences.getString("ACCESS_TOKEN", "None")

        val url = "http://10.0.2.2:8000/items/inventoryItems"
        val inv_id = arguments?.getString("inv_id")!!

        val img = R.drawable.img1
        val bodyParams = JSONObject().apply {
            put("inventoryId", inv_id)

        }
        val params = mapOf(
            "inventoryId___" to inv_id
        )

        val getRequest = CustomReq1(url, access_token!!, params, bodyParams, { response ->
            val gson = Gson()
            val ListType = object : TypeToken<ArrayList<inv_itemsItem>>() {}.type
            val users: ArrayList<inv_itemsItem> = gson.fromJson(response, ListType)
            for (i in users) {
                product.add(i)
            }
//            load_data()
            Log.d("API_CHECK", "Response: ${response.get(0)}")

        }, { error ->
            Log.d("API_CHECK", "Error: ${String(error.networkResponse.data)}")
        })

        Volley.newRequestQueue(requireContext()).add(getRequest)
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

    private fun showNotification(title: String, message: String) {
        val notificationManager =
            requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = System.currentTimeMillis().toInt()
        // Create an intent for the activity you want to open when the notification is clicked
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        // You can add extras to the intent if you need to pass data to the activity

        val pendingIntent = PendingIntent.getActivity(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "my_channel_id"
            val channel =
                NotificationChannel(channelId, "My Channel", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(requireContext(), channelId)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.logistics)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent) // Attach the pending intent to the notification
                .build()

            notificationManager.notify(notificationId, notification)
        } else {
            val notification = NotificationCompat.Builder(requireContext())
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.logistics)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent) // Attach the pending intent to the notification
                .build()

            notificationManager.notify(notificationId, notification)
        }
    }

    private fun monitorStock() {
        val stockRef =
            fs.collection("Product").document(auth.currentUser?.uid!!).collection("MyProduct")

        stockRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("D_CHECK", "Listen failed", e)
                return@addSnapshotListener
            }
            if (snapshot != null && !snapshot.isEmpty) {
                for (doc in snapshot.documents) {
                    val stock = doc.toObject(inv_itemsItem::class.java)
                    if (stock != null && stock.Stock!!.toInt() < stock.LowStock!!.toInt()) {
                        // If stock is below 5, call showNotification function
                        showNotification(
                            "Low Stock Alert",
                            "Stock for ${stock.ItemName} is below ${stock.LowStock}"
                        )
                    }
                }
            } else {
                Log.d("Hello", "No stock data")
            }
        }
    }


}

