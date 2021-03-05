package com.smarttech.story.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.smarttech.story.model.Category
import com.smarttech.story.model.Story

class StoryViewModel ( private val categoryId: Long): ViewModel() {

    private val _stories = MutableLiveData<ArrayList<Story>>().apply {
        // Access a Cloud Firestore instance from your Activity
        val db = Firebase.firestore
        val storiesRef = db.collection("story").whereEqualTo("category_id", categoryId)
        storiesRef.get().addOnSuccessListener { docs ->
            var stories = ArrayList<Story>()
            for (doc in docs) {
                stories!!.add(doc.toObject(Story::class.java))
            }
            value = stories
        }
    }
    var stories: LiveData<ArrayList<Story>> = _stories
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