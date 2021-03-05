package com.smarttech.story.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.smarttech.story.model.Category

class CategoryViewModel : ViewModel() {
    val db = Firebase.firestore
    private val _categories = MutableLiveData<ArrayList<Category>>().apply {
        // Access a Cloud Firestore instance from your Activity
        val categories = db.collection("category")
        categories.get().addOnSuccessListener { docs ->
            var categories = ArrayList<Category>()
            for (doc in docs) {
                categories!!.add(doc.toObject(Category::class.java))
            }
            value = categories
        }
    }
    var categories: LiveData<ArrayList<Category>> = _categories

    /**
     * Navigation for the SleepDetail fragment.
     */
    private val _category = MutableLiveData<Category?>()
    val navigateToCategory
        get() = _category

    fun onCategoryClicked(id: Long) {
        db.collection("category").whereEqualTo("id", id).get()
            .addOnSuccessListener { docs ->
                _category.value = docs.documents.get(0).toObject(Category::class.java)
            }
    }

    fun onCategoryNavigated() {
        _category.value = null
    }
}