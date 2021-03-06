package com.smarttech.story.ui.bookself

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.smarttech.story.R
import com.smarttech.story.database.AppDatabase
import com.smarttech.story.model.Function

class BookSelfViewModel() : ViewModel() {
    private lateinit var db: AppDatabase;
    private val _functions = MutableLiveData<List<Function>>().apply {
        var functions = ArrayList<Function>()
        functions.add(Function(1, "Tải về", "Truyện đã được tải về máy. Bạn có thể đọc không cần mạng", R.drawable.ic_baseline_cloud_download_24))
        functions.add(Function(2, "Lịch sử", "Lịch sử đọc truyện gần đây", R.drawable.ic_baseline_history_24))
        functions.add(Function(3, "Đánh dấu", "Truyện đã được đánh dấu", R.drawable.ic_baseline_bookmark_24))
        value = functions
    }
    var functions: LiveData<List<Function>> = _functions

    /**
     * Navigation for the SleepDetail fragment.
     */
    private val _function = MutableLiveData<Function?>()
    val navigateToFunction
        get() = _function

    fun onFunctionClicked(function: Function) {
        _function.value =function
    }

    fun onFunctionNavigated() {
        _function.value = null
    }
}