package com.smarttech.story.ui.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.smarttech.story.model.Category

class CategoryViewModel : ViewModel() {
    private lateinit var database: DatabaseReference
    private val _categories = MutableLiveData<ArrayList<Category>>().apply {
        // Access a Cloud Firestore instance from your Activity
        database = Firebase.database.reference
        val categories = database.child("data").child("categories")
        categories.get().addOnSuccessListener { docs ->
            var categories = ArrayList<Category>()
            for (doc in docs.children) {
                var  catetory = Category()
                catetory.id = doc.child("id").value.toString().toLong()
                catetory.title = doc.child("title").value.toString()
                categories!!.add(catetory)
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
        _category.value = _categories.value?.filter { category -> category.id == id }?.last()
/*        db.collection("category").whereEqualTo("id", id).get()
            .addOnSuccessListener { docs ->
                _category.value = docs.documents.get(0).toObject(Category::class.java)
            }*/
    }

    fun onCategoryNavigated() {
        _category.value = null
    }
}