package com.example.dummy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class profileViewmodel @Inject constructor() :ViewModel() {

    private val firebase = Firebase.firestore

    val _data= MutableStateFlow<Resource<List<PostData>>>(Resource.unspecified())
    val data: StateFlow<Resource<List<PostData>>> = _data


    init {
        fetchdata()
    }

    private fun fetchdata() {

        viewModelScope.launch {
            _data.emit (Resource.Loading())
        }

        firebase.collection("posts")
            .whereEqualTo("post","post").get().addOnSuccessListener { result ->
                val ProductList=result.toObjects(PostData::class.java)
                viewModelScope.launch {
                    _data.emit(Resource.Success(ProductList))
                }

            }

            .addOnFailureListener {
                viewModelScope.launch {
                    _data.emit(Resource.Error(it.message.toString()))
                }
            }
    }




}