package com.example.ims

import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.bumptech.glide.Glide
import com.example.ims.databinding.CustomProgressBinding
import com.example.ims.databinding.FragmentProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.math.log

class ProfileFragment : Fragment() {
    lateinit var fs: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var sr: StorageReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    lateinit var binding: FragmentProfileBinding
    private var galleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
//                    galleryprofile()
                val dialog = Dialog(requireContext())
                val layout = CustomProgressBinding.inflate(layoutInflater)
                dialog.setContentView(layout.root)
                dialog.setCanceledOnTouchOutside(false)
                dialog.show()
//                imageUri = result?.data!!.data!!
                binding.profilePic.setImageURI(result.data!!.data)
                sr = FirebaseStorage.getInstance()
                    .getReference("Profile")
                    .child(auth.currentUser?.uid!!)
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
        sharedPreferences =
            requireContext().getSharedPreferences("USERDATA", AppCompatActivity.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        loadData()
        var uid = ""
        var uname = ""
        var email = ""
        fs.collection("Users").document(auth.currentUser?.uid!!).get().addOnSuccessListener {
            var isAdmin = it.get("Admin") as Boolean
            if (isAdmin) {
                binding.adminIcon.visibility = View.VISIBLE
//                binding.textView.text = "Admin"
            } else {
                binding.adminIcon.visibility = View.GONE
//                binding.textView.text = "User"

            }
            uid = it.get("Uid") as String
            uname = it.get("Uname") as String
            email = it.get("Email") as String
            binding.uname.text = uname
            binding.username.text = uname
            binding.email.text = email

        }

        binding.profilePic.setOnClickListener {
            requestpermission()
        }

        binding.logoutCard.setOnClickListener {
            MaterialAlertDialogBuilder(
                requireContext()
            )
                .setTitle("Log Out")
                .setIcon(R.drawable.logout_24px)
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes") { dialog, which ->
                    editor.clear()
                    editor.commit()
                    auth.signOut()
                    val intent = Intent(requireContext(), Home_act::class.java)
                    startActivity(intent)
                    requireActivity().finish()

                }
                .setNegativeButton("No") { dialog, which ->
                    dialog.dismiss()
                }
                .show();
        }

        binding.resetCard.setOnClickListener {
            if (binding.email.text.toString().isNotEmpty()) {
                Firebase.auth.sendPasswordResetEmail(binding.email.text.toString())
                    .addOnSuccessListener {
                        Log.d("hello", "Email sent.")
                        custom_snackbar("Reset Mail Sent")
                    }.addOnFailureListener {
                        Log.d("hello", "Failed")
                        custom_snackbar("Failed to send mail. Try Again Later!")
                    }
            }
        }

        binding.emailCard.setOnLongClickListener {
            val clipboardManager =
                requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", email)
            Log.d("D_CHECK", "onViewCreated: $email ")
            clipboardManager.setPrimaryClip(clipData)
            true
        }
        binding.unameCard.setOnLongClickListener {
            val clipboardManager =
                requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", uname)
            Log.d("D_CHECK", "onViewCreated: $uname ")
            clipboardManager.setPrimaryClip(clipData)
            true
        }
    }

    fun loadData() {
        sr = FirebaseStorage.getInstance()
            .getReference("Profile")
            .child(auth.currentUser?.uid!!)
        sr.downloadUrl.addOnSuccessListener {
            Glide.with(requireContext()).load(it).into(binding.profilePic)
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

    private fun custom_snackbar(message: String) {
        val bar = Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
        bar.setBackgroundTint(resources.getColor(R.color.blue))
        bar.setAction("OK") {
            bar.dismiss()
        }
        bar.setActionTextColor(resources.getColor(R.color.blue3))
        bar.show()
    }
}