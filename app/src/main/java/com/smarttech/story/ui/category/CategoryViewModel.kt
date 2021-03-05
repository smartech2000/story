package com.smarttech.story.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.smarttech.story.model.Category

class CategoryViewModel : ViewModel() {

    private val _categories = MutableLiveData<ArrayList<Category>>().apply {
        // Access a Cloud Firestore instance from your Activity
        val db = Firebase.firestore
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
    private val _navigateToCategoryetail = MutableLiveData<Long?>()
    val navigateToSleepDetail
        get() = _navigateToCategoryetail

    fun onCategoryClicked(id: Long) {
        _navigateToCategoryetail.value = id
    }

    fun onCategoryNavigated() {
        _navigateToCategoryetail.value = null
    }
}