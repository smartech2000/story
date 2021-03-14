package com.smarttech.story.ui.category

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.smarttech.story.database.AppDatabase
import com.smarttech.story.model.Category

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var db: AppDatabase
    private val _categories = MutableLiveData<List<Category>>().apply {
        val storyDao = AppDatabase(application).storyDao()
        value = storyDao.getAllCategory()
    }
    var categories: LiveData<List<Category>> = _categories

    /**
     * Navigation for the SleepDetail fragment.
     */
    private val _category = MutableLiveData<Category?>()
    val navigateToCategory
        get() = _category

    fun onCategoryClicked(id: Int) {
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