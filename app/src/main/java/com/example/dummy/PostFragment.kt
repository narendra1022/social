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
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.dummy.databinding.FragmentPostBinding
import com.example.dummy.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.*


class PostFragment : Fragment() {

    private lateinit var binding: FragmentPostBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth
    private var uri: Uri? = null
    private var nm: String? = null
    private var lo: String? = null
    private var post: String? = null
    private var prof: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPostBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.postImg.setOnClickListener {
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

            FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().uid!!).get().addOnSuccessListener {
                    nm = it.get("name") as String?
                    lo = it.get("userLocation") as String?
                    prof = it.get("profilePicUrl") as String?
                }

        }

        binding.upload.setOnClickListener {

            val ca = binding.cap.text.toString()

            val data = PostData(ca, post, nm, prof, lo, "post")

            FirebaseFirestore.getInstance().collection("posts")
                .add(data).addOnSuccessListener {
                    Toast.makeText(requireContext(), "added", Toast.LENGTH_SHORT).show()
                }

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            if (data.data != null) {
                uri = data.data!!
                binding.postImg.setImageURI(data.data!!)
            }
        }


        val sReference =
            FirebaseStorage.getInstance().reference.child("Images").child("post_folder").child(
                Date().time.toString()
            )

        uri?.let { it1 ->
            sReference.putFile(it1).addOnCompleteListener {
                if (it.isSuccessful) {
                    sReference.downloadUrl.addOnSuccessListener { task ->

                        post = task.toString()


                    }
                }
            }

                .addOnFailureListener {
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

    }

}