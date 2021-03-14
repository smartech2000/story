package com.smarttech.story.ui.story

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.smarttech.story.database.AppDatabase
import com.smarttech.story.model.Category
import com.smarttech.story.model.CategoryStories
import com.smarttech.story.model.Story
import kotlin.system.measureTimeMillis

class StoryViewModel(application: Application, private val categoryId: Int, private val categoryName: String) : AndroidViewModel(application)  {
    private lateinit var database: DatabaseReference
    private lateinit var db: AppDatabase
    private val _stories = MutableLiveData<List<Story>>().apply {
        val storyDao = AppDatabase(application).storyDao()
        value = storyDao.getStoryByCategoryId(categoryId)
        // Access a Cloud Firestore instance from your Activity
/*        database = Firebase.database.reference
        var categoryStoriesList = ArrayList<CategoryStories>()
        // call your function here
        val categoryStoriesDbRef = database.child("data").child("categoryStories")
        //val stories = database.child("data").child("stories").
        categoryStoriesDbRef.get().addOnSuccessListener { docs ->
            val time = measureTimeMillis {
                for (doc in docs.children) {
                    var  categoryStories = CategoryStories(doc.child("category_id").value.toString().toLong(), doc.child("story_id").value.toString().toLong())
                    categoryStoriesList!!.add(categoryStories)
                }
            }
            var x = 0
        }
        var x = 0*/
        // Access a Cloud Firestore instance from your Activity
/*        val db = Firebase.firestore
        var stories = ArrayList<Story>()
        if (categoryId != -1L) {
            val storiesRef = db.collection("story").whereEqualTo("category_id", categoryId)
            storiesRef.get().addOnSuccessListener { docs ->
                for (doc in docs) {
                    stories!!.add(doc.toObject(Story::class.java))
                    value = stories
                }
            }
        } else {
            val storiesRef = db.collection("story");
            storiesRef.get().addOnSuccessListener { docs ->
                for (doc in docs) {
                    var   story = doc.toObject(Story::class.java)
                    if (story.name.contains(categoryName, true) || story.author.contains(categoryName, true))  {
                        stories.add(story)
                    }
                    value = stories
                }
            }
        }*/

    }
    var stories: LiveData<List<Story>> = _stories

    /**
     * Navigation for the SleepDetail fragment.
     */
    private val _navigateToCategoryetail = MutableLiveData<Int?>()
    val navigateToSleepDetail
        get() = _navigateToCategoryetail

    fun onCategoryClicked(id: Int) {
        _navigateToCategoryetail.value = id
    }

    fun onCategoryNavigated() {
        _navigateToCategoryetail.value = null
    }
}