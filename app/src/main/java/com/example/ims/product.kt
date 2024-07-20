package com.example.ims

import android.icu.text.NumberFormat
import android.icu.util.Currency
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ims.databinding.FragmentProductBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.Slider
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class product : Fragment() {
    private lateinit var productAdapter: product_adapter
    lateinit var binding: FragmentProductBinding
    lateinit var previewDialog: BottomSheetDialog
    lateinit var auth: FirebaseAuth
    lateinit var fs: FirebaseFirestore
    lateinit var sr: StorageReference
    lateinit var Uid: String
    private var isAnyCategoryChipClicked = false
    lateinit private var filter: ArrayList<String>
    lateinit private var filter_name: ArrayList<String>
    lateinit var productList: ArrayList<inv_itemsItem>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
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
        Uid = arguments?.getString("uid").toString()
        previewDialog = BottomSheetDialog(requireContext())
        auth = FirebaseAuth.getInstance()
        fs = FirebaseFirestore.getInstance()
        productList = arrayListOf()
        filter = arrayListOf()
        filter_name = arrayListOf()
//        get_data()
        checkUser()
        binding.filterProduct.setOnClickListener {

            filter.clear()
            val filterDialog = BottomSheetDialog(requireContext())
            filterDialog.setContentView(R.layout.filterdialog)

            filterDialog.findViewById<Button>(R.id.clear)?.setOnClickListener {
                checkUser()
                filterDialog.dismiss()
            }
            getCategoriesFromFirestore { categories ->
                for (category in categories) {
                    val chip = Chip(requireContext())
                    chip.apply {
                        text = category
                        isCheckable = true
                        setChipDrawable(
                            ChipDrawable.createFromAttributes(
                                requireContext(),
                                null,
                                0,
                                com.google.android.material.R.style.Widget_MaterialComponents_Chip_Filter
                            )
                        )


                        setOnClickListener {
                            isAnyCategoryChipClicked = true
                            filter.add(category)
                            Log.d(
                                "D_CHECK",
                                "onViewCreated filter list: ${filter} "
                            )
                        }
                        filterDialog.apply {

                            filterDialog.findViewById<ChipGroup>(R.id.chipGroup)
                                ?.addView(chip as View)
                        }
                    }
                }
            }

            getProductFromFirestore { categories ->
                for (category in categories) {
                    val chip = Chip(requireContext())
                    chip.apply {
                        text = category
                        isCheckable = true
                        setChipDrawable(
                            ChipDrawable.createFromAttributes(
                                requireContext(),
                                null,
                                0,
                                com.google.android.material.R.style.Widget_MaterialComponents_Chip_Filter
                            )
                        )


                        setOnClickListener {
                            isAnyCategoryChipClicked = true
                            filter_name.add(category)
                            Log.d(
                                "D_CHECK",
                                "onViewCreated filter list: ${filter} "
                            )
                        }
                        filterDialog.apply {

                            filterDialog.findViewById<ChipGroup>(R.id.chipGroup2)
                                ?.addView(chip as View)
                        }
                    }
                }
            }
            if (filterDialog.findViewById<TabLayout>(R.id.tabLayout)?.selectedTabPosition == 0) {
                filterDialog.findViewById<Button>(R.id.show)?.setOnClickListener {

                    val list =
                        productList.filter {
                            it.PricePerUnit?.toInt()!! in 0..filterDialog.findViewById<Slider>(
                                R.id.price_slider
                            )?.value?.toInt()!!
                        }
                    updateUI(list as ArrayList<inv_itemsItem>)
                    filterDialog.dismiss()
                }
            }
            filterDialog.findViewById<TabLayout>(R.id.tabLayout)
                ?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        when (tab?.position) {
                            1 -> {
                                //category
                                filterDialog.findViewById<Button>(R.id.clear)?.setOnClickListener {
                                    checkUser()
                                    filterDialog.dismiss()
                                }
                                filterDialog.findViewById<LinearLayout>(R.id.category)?.visibility =
                                    View.VISIBLE
                                filterDialog.findViewById<LinearLayout>(R.id.price)?.visibility =
                                    View.GONE
                                filterDialog.findViewById<LinearLayout>(R.id.stock)?.visibility =
                                    View.GONE
                                filterDialog.findViewById<LinearLayout>(R.id.product)?.visibility =
                                    View.GONE
                                filterDialog.findViewById<Button>(R.id.show)?.setOnClickListener {
                                    val list =
                                        productList.filter {
                                            it.Category in filter
                                        }
                                    updateUI(list as ArrayList<inv_itemsItem>)
                                    filterDialog.dismiss()
                                }
                            }

                            0 -> {
                                //price
                                filterDialog.findViewById<Button>(R.id.clear)?.setOnClickListener {
                                    checkUser()
                                    filterDialog.dismiss()
                                }
                                filterDialog.findViewById<LinearLayout>(R.id.category)?.visibility =
                                    View.GONE
                                filterDialog.findViewById<LinearLayout>(R.id.price)?.visibility =
                                    View.VISIBLE
                                filterDialog.findViewById<LinearLayout>(R.id.stock)?.visibility =
                                    View.GONE
                                filterDialog.findViewById<LinearLayout>(R.id.product)?.visibility =
                                    View.GONE
                                filterDialog.findViewById<Button>(R.id.show)?.setOnClickListener {
                                    Log.d("D_CHECK", "onItemLongClick: ${tab.position}")

                                    val list =
                                        productList.filter {
                                            it.PricePerUnit?.toInt()!! in 0..filterDialog.findViewById<Slider>(
                                                R.id.price_slider
                                            )?.value?.toInt()!!
                                        }
                                    updateUI(list as ArrayList<inv_itemsItem>)
                                    filterDialog.dismiss()
                                }
                            }

                            2 -> {

                                filterDialog.findViewById<LinearLayout>(R.id.category)?.visibility =
                                    View.GONE
                                filterDialog.findViewById<LinearLayout>(R.id.price)?.visibility =
                                    View.GONE
                                filterDialog.findViewById<LinearLayout>(R.id.stock)?.visibility =
                                    View.VISIBLE
                                filterDialog.findViewById<LinearLayout>(R.id.product)?.visibility =
                                    View.GONE
                                filterDialog.findViewById<Button>(R.id.show)?.setOnClickListener {
                                    Log.d("D_CHECK", "onItemLongClick: ${tab.position}")

                                    val list =
                                        productList.filter {
                                            it.Stock?.toInt()!! in 0..filterDialog.findViewById<Slider>(
                                                R.id.stock_slider
                                            )?.value?.toInt()!!
                                        }
                                    updateUI(list as ArrayList<inv_itemsItem>)
                                    filterDialog.dismiss()
                                }
                            }

                            3 -> {
                                filterDialog.findViewById<LinearLayout>(R.id.product)?.visibility =
                                    View.VISIBLE
                                filterDialog.findViewById<LinearLayout>(R.id.category)?.visibility =
                                    View.GONE
                                filterDialog.findViewById<LinearLayout>(R.id.price)?.visibility =
                                    View.GONE
                                filterDialog.findViewById<LinearLayout>(R.id.stock)?.visibility =
                                    View.GONE

                                filterDialog.findViewById<Button>(R.id.show)?.setOnClickListener {
                                    val a = productList.filter { it.ItemName in filter_name }

                                    updateUI(java.util.ArrayList(a))
                                    filterDialog.dismiss()
                                }
                            }
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {
                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {
                    }

                })

            filterDialog.findViewById<Slider>(R.id.price_slider)
                ?.addOnChangeListener { slider, value, fromUser ->
                    filterDialog.findViewById<TextView>(R.id.price_range_view)?.text = "0 to $value"
                }
            filterDialog.findViewById<Slider>(R.id.stock_slider)
                ?.addOnChangeListener { slider, value, fromUser ->
                    filterDialog.findViewById<TextView>(R.id.stock_range_view)?.text = "0 to $value"
                }


            filterDialog.findViewById<Slider>(R.id.price_slider)
                ?.setLabelFormatter { value: Float ->
                    val format = NumberFormat.getCurrencyInstance()
                    format.maximumFractionDigits = 0
                    format.currency = Currency.getInstance("INR")
                    format.format(value.toDouble())
                }

            filterDialog.findViewById<Slider>(R.id.stock_slider)
                ?.setLabelFormatter { value: Float ->
                    val format = NumberFormat.getNumberInstance()
                    format.maximumFractionDigits = 0
                    format.format(value.toDouble())
                }

            // Filter Category


//
            filterDialog.show()
        }
        binding.toggleButton.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            if (isChecked) {
//                clearIcons(toggleButton)
                // Set icon on the selected button
//                val selectedButton = binding.toggleButton.findViewById<Button>(checkedId)
//                selectedButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.product, 0, 0, 0)

                when (checkedId) {
                    R.id.Category -> sort_data(1)
                    R.id.Time -> sort_data(2)
                    R.id.Name -> sort_data(3)
                    R.id.Stock -> sort_data(4)
                    else -> checkUser() //get_data()
                }
            } else {
//                get_data()
                checkUser()
//                val unselectedButton = binding.toggleButton.findViewById<Button>(checkedId)
//                unselectedButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }

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

    private fun clearIcons(group: MaterialButtonToggleGroup) {
        for (i in 0 until group.childCount) {
            val button = group.getChildAt(i) as Button
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
        }
    }

    private fun simulateDataLoading() {
        binding.ProgressBar.postDelayed({
            binding.rvProduct.visibility = View.VISIBLE

        }, 2000)
    }

    private fun get_data() {

        fs.collection("Product").document(auth.currentUser?.uid!!).collection("MyProduct")
            .orderBy("CreatedAt", Query.Direction.DESCENDING).get().addOnSuccessListener {

                productList.clear()
                for (data in it) {
                    val r = data.toObject(inv_itemsItem::class.java)
                    productList.add(r)
                }
                updateUI(productList)

            }
    }

    private fun sort_data(sortDetails: Int) {

        fs.collection("Product").document(auth.currentUser?.uid!!).collection("MyProduct")
            .orderBy("CreatedAt", Query.Direction.DESCENDING).get().addOnSuccessListener {

                productList.clear()
                for (data in it) {
                    val r = data.toObject(inv_itemsItem::class.java)
                    productList.add(r)
                }
                when (sortDetails) {
                    1 -> {
                        productList.sortBy { it.Category }
                    }

                    2 -> {
                        productList.sortBy { it.CreatedAt }
                    }

                    3 -> {
                        productList.sortBy { it.ItemName }
                    }

                    4 -> {
                        productList.sortBy { it.Stock }
                    }
                }
                updateUI(productList)

            }
    }

    private fun updateUI(items: ArrayList<inv_itemsItem>) {
        productAdapter = product_adapter(items)
        binding.rvProduct.adapter = productAdapter
        binding.rvProduct.viewTreeObserver.addOnGlobalLayoutListener {
            binding.ProgressBar.visibility = View.GONE
        }
        simulateDataLoading()
        productAdapter.onItem(object : product_adapter.onitemclick {
            override fun itemClickListener(position: Int) {
                Log.d("hello", "itemClickListener: ${position}")
                val view = View.inflate(requireContext(), R.layout.preview_dialog, null)
                sr = FirebaseStorage.getInstance()
                    .getReference("Product/" + items[position].UserId)
                    .child("Inv${items[position].InventoryId}_Product${items[position].ProductId}")

                view.findViewById<ImageView>(R.id.P_img).setPadding(0, 0, 0, 0)
                sr.downloadUrl.addOnSuccessListener {
                    Glide.with(requireContext()).load(it).into(view.findViewById(R.id.P_img))
                }

                val imageView = view.findViewById<ImageView>(R.id.P_img)
                sr.downloadUrl.addOnSuccessListener {
                    Glide.with(requireContext()).load(it).into(imageView)
                }
                previewDialog.setContentView(view)
                previewDialog.show()
                previewDialog.setCancelable(true)
                previewDialog.setCanceledOnTouchOutside(true)

                view.findViewById<TextView>(R.id.product_name).text = items[position].ItemName
                view.findViewById<TextView>(R.id.product_unit).text = items[position].Stock
                view.findViewById<TextView>(R.id.category).text = items[position].Category
                view.findViewById<TextView>(R.id.P_id).text = items[position].ProductId
                view.findViewById<TextView>(R.id.pp_unit).text = items[position].PricePerUnit + "/-"
                view.findViewById<Button>(R.id.notify).visibility = View.GONE
                view.findViewById<MaterialButton>(R.id.delete).visibility = View.GONE

            }

        })

    }

    private fun s_getdata() {
        productList.clear()
        fs.collection("Users").get().addOnSuccessListener {
            for (data in it) {
                Log.d("D_CHECK", "s_getinventory: ${data.id}")
                fs.collection("Product").document(data.id).collection("MyProduct")
                    .orderBy("CreatedAt", Query.Direction.DESCENDING).get().addOnSuccessListener {
                        for (data in it) {
                            val r = data.toObject(inv_itemsItem::class.java)
                            productList.add(r)
                        }
                        updateUI(productList)

                    }
            }
        }
    }

    private fun checkUser() {
        fs.collection("Users").document(auth.currentUser?.uid!!).get().addOnSuccessListener {
            var isAdmin = it.get("Admin") as Boolean
            if (isAdmin) {
                s_getdata()
            } else {
                get_data()
            }
        }
    }

    fun getCategoriesFromFirestore(onResult: (List<String>) -> Unit) {
// Replace with your collection name
        fs.collection("Users").get().addOnSuccessListener {
            for (data in it) {
                Log.d("D_CHECK", "s_getinventory: ${data.id}")
                fs.collection("Product").document(data.id).collection("MyProduct")
                    .orderBy("CreatedAt", Query.Direction.DESCENDING).get()
                    .addOnSuccessListener { result ->

                        val categories = result.documents.mapNotNull { document ->
                            document.getString("Category")
                        }.distinct() // To remove duplicates if necessary
                        categories.distinct()
                        onResult(categories)
                    }.addOnFailureListener { exception ->
                        // Handle the error
                        onResult(emptyList())
                    }

            }
        }
    }

    fun getProductFromFirestore(onResult: (List<String>) -> Unit) {
// Replace with your collection name
        fs.collection("Users").get().addOnSuccessListener {
            for (data in it) {
                fs.collection("Product").document(data.id).collection("MyProduct")
                    .orderBy("CreatedAt", Query.Direction.DESCENDING).get()
                    .addOnSuccessListener { result ->
                        val productName = result.documents.mapNotNull { document ->
                            document.getString("ItemName")
                        }.distinct() // To remove duplicates if necessary
                        productName.distinct()
                        Log.d("D_CHECK", "getProductFromFirestore: $productName")
                        onResult(productName)
                    }
                    .addOnFailureListener { exception ->
                        // Handle the error
                        onResult(emptyList())
                    }
            }
        }
    }
}


