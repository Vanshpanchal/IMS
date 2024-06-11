package com.example.ims

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.ims.databinding.ActivitySignupBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.make
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class SignupAct : AppCompatActivity() {
    lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var fs: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        fs = FirebaseFirestore.getInstance()

        binding.SignupBtn.setOnClickListener {
//            signUpUser("visualcode780@gmail.com", "123456789")
            if(binding.username.text.toString().isNotBlank() && binding.email.text.toString().isNotEmpty() && binding.password.text.toString()
                    .isNotEmpty())
            signUpUser(binding.email.text.toString(), binding.password.text.toString())
//            finish()
//            finish()
        }
    }

    private fun signUpUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendEmailVerification()
                } else {
                    when (val exception = task.exception) {
                        is FirebaseAuthInvalidCredentialsException -> {
                            Log.d("hello", "Invalid email or password format: ${exception.message}")

                            val bar = make(binding.root, "Invalid format", Snackbar.LENGTH_SHORT)
                            bar.setBackgroundTint(getColor(R.color.blue))
                            bar.setAction("OK") {
                                bar.dismiss()
                            }
                            bar.setActionTextColor(getColor(R.color.blue3))
                            bar.show()
                        }

                        is FirebaseAuthUserCollisionException -> {
                            Log.d("hello", "Email address already in use: ${exception.message}")
                            val bar =
                                make(binding.root, "Email Already Exist", Snackbar.LENGTH_SHORT)
                            bar.setBackgroundTint(getColor(R.color.blue))
                            bar.setAction("OK") {
                                bar.dismiss()
                            }
                            bar.setActionTextColor(getColor(R.color.blue3))
                            bar.show()
                        }

                        is FirebaseAuthInvalidUserException -> {
                            Log.d("hello", "Invalid user: ${exception.message}")
                            val bar =
                                make(binding.root, "Invalid credential", Snackbar.LENGTH_SHORT)
                            bar.setBackgroundTint(getColor(R.color.blue))
                            bar.setAction("OK") {
                                bar.dismiss()
                            }
                            bar.setActionTextColor(getColor(R.color.blue3))
                            bar.show()
                        }

                        else -> {
                            Log.d("hello", "Sign-up failed: ${exception?.message}")
                            val bar = make(binding.root, "Else ‼️", Snackbar.LENGTH_SHORT)
                            bar.setBackgroundTint(getColor(R.color.blue))
                            bar.setAction("OK") {
                                bar.dismiss()
                            }
                            bar.setActionTextColor(getColor(R.color.blue3))
                            bar.show()
                        }
                    }
                }
            }
    }
    private fun sendEmailVerification() {
        val user = auth.currentUser

        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    val bar = make(binding.root, "Mail Sent", Snackbar.LENGTH_SHORT)
                    bar.setBackgroundTint(getColor(R.color.blue))
                    bar.setAction("OK") {
                        bar.dismiss()
                        val intent = Intent(this, SigninAct::class.java)
                        startActivity(intent)
                        finish()
                    }
                    bar.setActionTextColor(getColor(R.color.blue3))
                    bar.show()
                    val randomInt = Random.nextInt(0, 100)
                    val userData = hashMapOf(
                        "Email" to auth.currentUser?.email,
                        "Uid" to auth.currentUser?.uid,
                        "Uname" to binding.username.text.toString() + "firestore-$randomInt",
                        "Admin" to false
                    )

                    fs.collection("Users")
                        .document(auth.currentUser?.uid.toString())
                        .set(userData)

                } else {
                    Log.d("D_CHECK", "sendEmailVerification: $task.exception?.message")
                }
            }?.addOnFailureListener {
                val bar = make(binding.root, "An Error Occurred", Snackbar.LENGTH_SHORT)
                bar.setBackgroundTint(getColor(R.color.blue))
                bar.setAction("OK") {
                    bar.dismiss()
                }
                bar.setActionTextColor(getColor(R.color.blue3))
                bar.show()
            }
    }

}
