package com.example.ims

import android.app.Activity
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
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.text.input.KeyboardType.Companion.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
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

//import retrofit2.Response

class specific_inventory : Fragment() {
    lateinit var previewDialog: BottomSheetDialog
    lateinit var binding: FragmentSpecificInventoryBinding
    lateinit var product: ArrayList<inv_itemsItem>
    lateinit var inventory_id: String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    var imageView: Uri? = null


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
//        val data = product_list("Product 1", "Inventory 1", "4")
//        product.add(data)
//        product.add(data)
//        product.add(data)
//        product.add(data)

//        load_data()
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
            val product_img = view.findViewById<ImageView>(R.id.P_img)
            view.findViewById<ImageView>(R.id.P_img).setOnClickListener {
                openGallery()
//                product_img.setImageURI(ImageView!!)
            }
            view.findViewById<Button>(R.id.submit_btn).setOnClickListener {
                val url = "http://10.0.2.2:8000/items/add"
                Log.d("hello", "itemClickListener: $} ")
//                val img = drawableToByteArray(R.drawable.img2)
                val bodyParams = JSONObject().apply {
                    put("itemName", "Shoes")
                    put("itemId", "itm01")
                    put("pricePerUnit", "100")
                    put("stock", "10")
                    put("inventoryId", "05")
                    put("category", "Shoes")
                    put("itemimage",imageView)
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
                dialog.dismiss()

            }
            dialog.show()
        }
    }

    fun drawableToByteArray(context: Context, drawableId: Int): ByteArray {
        val drawable = context.getDrawable(drawableId)
        val bitmap = when (drawable) {
            is BitmapDrawable -> drawable.bitmap
            is VectorDrawable -> getBitmapFromVectorDrawable(drawable)
            else -> throw IllegalArgumentException("Unsupported drawable type")
        }
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        return stream.toByteArray()
    }

    private fun getBitmapFromVectorDrawable(vectorDrawable: VectorDrawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return bitmap
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

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 50)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 50 && resultCode == Activity.RESULT_OK) {
            imageView= data?.data
//            imageView = selectedImageUri
//            binding.setImageURI(selectedImageUri)
        }
    }


}

