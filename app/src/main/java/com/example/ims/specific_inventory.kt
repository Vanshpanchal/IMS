package com.example.ims

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.ui.text.input.KeyboardType.Companion.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.util.query
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.ims.databinding.FragmentSpecificInventoryBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import kotlin.math.log

//import retrofit2.Response

class specific_inventory : Fragment() {


    lateinit var previewDialog: BottomSheetDialog
    lateinit var binding: FragmentSpecificInventoryBinding
    lateinit var product: ArrayList<inv_itemsItem>
    lateinit var inventory_id: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    var imageView: Uri? = null
    private var product_img: ImageView? = null

    private var galleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
//                    galleryprofile()
                product_img?.setImageURI(result.data!!.data)
                imageView = result.data!!.data!!
                Log.d("TAG", "result:${result.data!!.data} ")
            }
        }

    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSpecificInventoryBinding.inflate(inflater, container, false)

        // Retrieve the data from the arguments
        val inv_name = arguments?.getString("inv_name")
        binding.textView.text = inv_name
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

        val getRequest = CustomReq1(
            url,
            access_token!!,
            params,
            bodyParams,
            { response ->
                val gson = Gson()
                val ListType = object : TypeToken<ArrayList<inv_itemsItem>>() {}.type
                val users: ArrayList<inv_itemsItem> = gson.fromJson(response, ListType)
                for (i in users) {
                    product.add(i)
                }
                load_data()
                Log.d("API_CHECK", "Response: ${response.get(0)}")

            },
            { error ->
                Log.d("API_CHECK", "Error: ${String(error.networkResponse.data)}")
            }
        )

        Volley.newRequestQueue(requireContext()).add(getRequest)

        binding.addProduct.setOnClickListener {
            val view = LayoutInflater.from(requireContext())
                .inflate(R.layout.product_add_dialog, null, false)
            val dialog = MaterialAlertDialogBuilder(
                requireContext()
            )
                .setView(view)
                .create()
            product_img = view.findViewById<ImageView>(R.id.P_img)
            view.findViewById<ImageView>(R.id.P_img).setOnClickListener {
                requestpermission()
            }
            view.findViewById<Button>(R.id.submit_btn).setOnClickListener {
                Log.d("TAG", "result:${imageView} ")
                add_product(inv_id, access_token, imageView!!)
                dialog.dismiss()

            }
            dialog.show()
        }
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
                val imageView = view.findViewById<ImageView>(R.id.P_img)
                val imageUrl = product[position].itemImageurl
                Glide.with(requireContext())
                    .load(imageUrl)
                    .into(imageView)
                view.findViewById<TextView>(R.id.product_name).text = product[position].itemName
                view.findViewById<TextView>(R.id.product_unit).text =
                    product[position].stock.toString()
                view.findViewById<TextView>(R.id.inv_id).text = product[position].inventoryId
                view.findViewById<TextView>(R.id.category).text = product[position].category
                view.findViewById<TextView>(R.id.pp_unit).text =
                    product[position].pricePerUnit.toString() + "/-"
                view.findViewById<TextView>(R.id.P_id).text = product[position].itemId

            }

        })
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
                    requireContext() as Activity,
                    permissiontoRequest.toTypedArray(),
                    0
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
                    requireContext() as Activity,
                    permissiontoRequest.toTypedArray(),
                    0
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

    fun add_product(inv_id: String, access_token: String ,imageView: Uri) {
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
        val getRequest = CustomReq1(
            url,
            access_token!!,
            params,
            bodyParams,
            { response ->

                Log.d("API_CHECK", "Response: ${response}")

            },
            { error ->
                Log.d("API_CHECK", "Error: ${String(error.networkResponse.data)}")
            }
        )

        Volley.newRequestQueue(requireContext()).add(getRequest)
    }

}

