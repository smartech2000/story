package com.smarttech.story.ui.story.detail.desc

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.smarttech.story.database.DescDatabase
import com.smarttech.story.global.ApplicationState
import kotlinx.coroutines.*

class StoryDescViewModel(
    application: Application,
    private val storyId: Int
) : AndroidViewModel(application) {
    private lateinit var descDb: DescDatabase
    // TODO: Implement the ViewModel
    private val _storyDesc = MutableLiveData<String>().apply {
        GlobalScope.launch(Dispatchers.IO) {
            var timeOut = 0L;
            var dbExist = false;
            while (!ApplicationState.storyDescDbDone.get() && timeOut < 30000) {
                timeOut += 1000L
                delay(1000L)
            }
            withContext(Dispatchers.Main) {
                val storyDescDao = DescDatabase(application).storyDescDao()
                val storyDesc = storyDescDao.findStoryDescById(storyId)
                if (storyDesc != null) {
                    value = storyDesc.description
                } else {
                    value=""
                }
            }
        }
    }

    val storyDesc: LiveData<String> = _storyDesc

}