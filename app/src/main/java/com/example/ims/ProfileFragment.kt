package com.example.ims

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ims.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ProfileFragment : Fragment() {
    lateinit var fs: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var binding: FragmentProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fs = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        fs.collection("Users").document(auth.currentUser?.uid!!).get().addOnSuccessListener {
            var isAdmin = it.get("Admin") as Boolean
            if (isAdmin) {
              binding.textView.text = "Admin"
            } else {
                binding.textView.text = "User"

            }
        }
    }
}