package com.example.dummy

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.dummy.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth
    private var uri: Uri? = null
    private lateinit var st: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater)


        firebaseDatabase = FirebaseDatabase.getInstance(); firebaseAuth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        firebaseDatabase = FirebaseDatabase.getInstance(); firebaseAuth = FirebaseAuth.getInstance()

        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(FirebaseAuth.getInstance().uid!!).get()
            .addOnSuccessListener {

                if (it.get("profilePicUrl") != "") {
                    Glide.with(requireContext()).load(it.get("profilePicUrl"))
                        .into(binding.photo)
                    binding.editname.text=it.getString("name")
                }
            }



        binding.next.setOnClickListener {
            FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().uid!!)
                .update("userLocation", binding.location.text.toString()).addOnSuccessListener {

                }

            val intent = Intent(activity, loginActivity::class.java)
            startActivity(intent)

        }


        binding.photo.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MainActivity.READ_PERMISSION
                )
            }

            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)

            binding.addphototv.visibility = View.GONE

        }

        binding.editname.setOnClickListener {

            val mDialog = AlertDialog.Builder(requireContext())
            val inflater = layoutInflater
            val mDialogView = inflater.inflate(R.layout.edit_name, null)
            mDialog.setView(mDialogView)
            val addEmailbtn: Button = mDialogView.findViewById(R.id.addBtn)
            val addEmail: TextView = mDialogView.findViewById(R.id.addEmail)
            mDialog.setTitle("Edit name")
            val alertDialog = mDialog.create()
            alertDialog.show()

            addEmailbtn.setOnClickListener {

                val mail = addEmail.text.toString()
                if (mail.length.equals(0)) {
                    Toast.makeText(requireContext(), "Enter name", Toast.LENGTH_SHORT).show()
                } else {
                    FirebaseFirestore.getInstance().collection("users")
                        .document(FirebaseAuth.getInstance().uid!!)
                        .update("name", mail).addOnSuccessListener {
                            Toast.makeText(
                                requireContext(),
                                "Name Added Successfully",
                                Toast.LENGTH_SHORT
                            )
                                .show()


                            db.collection("users").document(FirebaseAuth.getInstance().uid!!).get()
                                .addOnSuccessListener {

                                        binding.editname.text=it.getString("name")
                                    }

                            alertDialog.dismiss()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                }


            }


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            if (data.data != null) {
                uri = data.data!!
                binding.photo.setImageURI(data.data!!)
            }
        }

        Toast.makeText(
            requireContext(),
            "Profile pic updated",
            Toast.LENGTH_SHORT
        ).show()

        val sReference =
            FirebaseStorage.getInstance().reference.child("Images").child("user_folder").child(
                Date().time.toString()
            )

        uri?.let { it1 ->
            sReference.putFile(it1).addOnCompleteListener {
                if (it.isSuccessful) {
                    sReference.downloadUrl.addOnSuccessListener { task ->

                        FirebaseFirestore.getInstance().collection("users")
                            .document(FirebaseAuth.getInstance().uid!!)
                            .update("profilePicUrl", task).addOnSuccessListener {

                            }
                    }
                }
            }

                .addOnFailureListener {
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

    }
}