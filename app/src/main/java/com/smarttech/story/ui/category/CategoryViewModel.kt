package com.smarttech.story.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.smarttech.story.model.Category

class CategoryViewModel : ViewModel() {

    private val _text = MutableLiveData<ArrayList<Category>>().apply {
        // Access a Cloud Firestore instance from your Activity
        val db = Firebase.firestore
        val categories = db.collection("category")
        categories.get().addOnSuccessListener { docs ->
            var categories = ArrayList<Category>()
            for (doc in docs) {
                var category = Category(doc.data.getValue("name") as String)
                categories!!.add(category)
            }
            value = categories
        }
    }
    var text: LiveData<ArrayList<Category>> = _text
}