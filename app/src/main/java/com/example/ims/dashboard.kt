package com.example.ims

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.ims.databinding.FragmentDashboardBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class dashboard : Fragment() {
    lateinit var binding: FragmentDashboardBinding
    lateinit var auth: FirebaseAuth
    lateinit var fs: FirebaseFirestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val msg = arrayOf(
            "Packages to be packed",
            "Packages to be shipped",
            "Packages to be delivered",
            "Packages to be Invoiced"
        )
        val num = arrayOf("450", "21", "10", "24")
        val images = arrayOf(
            R.drawable.logistics,
            R.drawable.logistics,
            R.drawable.logistics,
            R.drawable.logistics
        )
        auth = FirebaseAuth.getInstance()
        fs = FirebaseFirestore.getInstance()

        // Adapter
        val adapter = list_view_adapter(requireContext(), R.layout.list_view, msg, num, images)

        // ListView
        val listView: ListView = binding.listView
        listView.dividerHeight = 0
        listView.adapter = adapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val selectedItem = msg[position]
            Toast.makeText(requireContext(), "Selected Item: $selectedItem", Toast.LENGTH_SHORT)
                .show()
//        }

        }

//        listView.adapter = adapter
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkUser()

    }

    fun checkUser() {
        fs.collection("Users").document(auth.currentUser?.uid!!).get().addOnSuccessListener {
            var isAdmin = it.get("Admin") as Boolean
            if (isAdmin) {
                adminDashboard()
            }else{
                userDashboard()
            }
        }
    }

    fun userDashboard(){
        fs.collection("Product").document(auth.currentUser?.uid!!).collection("MyProduct").get()
            .addOnSuccessListener {
                binding.totalItems.text = it.size().toString()
                Log.d("D_CHECK", "onViewCreated: ${it.size()}")
            }

        fs.collection("Product").document(auth.currentUser?.uid!!).collection("MyProduct")
            .whereNotEqualTo("LowStock", "-1").get().addOnSuccessListener {
                binding.totalNotify.text = it.size().toString()
                Log.d("D_CHECK", "onViewCreated : ${it.size()}")
                var count = 0
                for (doc in it) {
                    Log.d("D_CHECK", "onViewCreated: ${doc.get("LowStock").toString()}")
                    if (doc.get("Stock").toString().toInt() < doc.get("LowStock").toString()
                            .toInt()
                    ) {
                        count++
                    }
                }
                binding.lowStockItems.text = count.toString()

            }
    }
    fun adminDashboard() {
        fs.collection("Users").get().addOnSuccessListener {
            var count_notify = 0
            var count_lowstock = 0
            for (data in it) {
                fs.collection("Product").document(data.id).collection("MyProduct")
                    .whereNotEqualTo("LowStock", "-1").get().addOnSuccessListener {
                        count_lowstock+=it.size()
                        Log.d("D_CHECK", "onViewCreated : ${it.size()}")
                        for (doc in it) {
                            Log.d("D_CHECK", "onViewCreated: ${doc.get("LowStock").toString()}")
                            if (doc.get("Stock").toString().toInt() < doc.get("LowStock").toString()
                                    .toInt()
                            ) {
                                count_notify++
                        binding.lowStockItems.text = count_notify.toString()
                            }
                        }
                        binding.totalNotify.text = it.size().toString()
                    }
            }

        }
        fs.collection("Users").get().addOnSuccessListener {
            var count=0
            for (data in it) {
                fs.collection("Product").document(data.id).collection("MyProduct").get()
                    .addOnSuccessListener {
                        count += it.size()
                        Log.d("D_CHECK", "onViewCreated__: ${it.size()}")
                        binding.totalItems.text = it.size().toString()
                    }
            }
            binding.totalItems.text = it.size().toString()
        }


    }
}
