package com.example.dummy

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.dummy.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class signUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val firebase = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {

            startActivity(Intent(this,loginActivity::class.java))
        }
        binding.button.setOnClickListener {

            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()
            val name = binding.nameEt.text.toString()
            val phone = binding.phoneEt.text.toString()

            val users = User(
                email,
                pass,
                name,
                phone,
                "profiles"
            )


            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {

                    if (pass.length > 6) {
                        if (pass == confirmPass) {

                            firebaseAuth.createUserWithEmailAndPassword(email, pass)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {

                                        firebase.collection("users").document(firebaseAuth.uid!!)
                                            .set(users)

                                        supportFragmentManager.beginTransaction()
                                            .add(R.id.sign, ProfileFragment()).commit()

                                        binding.button.visibility=View.INVISIBLE

                                    } else {
                                        Toast.makeText(this, "${it.exception} ", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                        } else {
                            Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Password must be greater than 6 digits",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                } else {
                    Toast.makeText(this, "Enter the correct EMAIL format", Toast.LENGTH_SHORT)
                        .show()
                }

            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show()

            }
        }
    }
}