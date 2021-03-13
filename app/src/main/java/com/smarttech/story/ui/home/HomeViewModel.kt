package com.smarttech.story.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.ktx.Firebase

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        // Access a Cloud Firestore instance from your Activity
/*        val db = Firebase.firestore
        val categories = db.collection("category")
        value = "This is home Fragment:"
        categories.get().addOnSuccessListener { docs ->
            for (doc in docs) {
                value = value + doc.data.getValue("name")
            }
        }*/


    }
    var text: LiveData<String> = _text
}