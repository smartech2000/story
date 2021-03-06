package com.smarttech.story.ui.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.smarttech.story.R
import com.smarttech.story.database.AppDatabase
import com.smarttech.story.model.Function

class AccountViewModel() : ViewModel() {
    private lateinit var db: AppDatabase;
    private val _functions = MutableLiveData<List<Function>>().apply {
        var functions = ArrayList<Function>()
        //functions.add(Function(1, "Cài đặt", "Cài đặt cấu hình ứng dụng", R.drawable.ic_baseline_settings_24))
        functions.add(Function(2, "Đánh giá ứng dụng", "Nếu bạn thấy ứng dựng hữu ích. Vui lòng nhấn vào đây để đánh giá trên Google Play. Chúng tôi xin chân thành cám ơn bạn!", R.drawable.ic_baseline_star_rate_24))
        functions.add(Function(3, "Chia sẻ", "Chia sẻ ứng dụng cho bạn bè", R.drawable.ic_baseline_share_24))
        functions.add(Function(4, "Kho ứng dụng", "Kho ứng dụng của Smart Tech", R.drawable.ic_baseline_play_arrow_24))
        functions.add(Function(5, "Thông tin liên hệ", "Thông tin liên hệ", R.drawable.ic_baseline_contact_phone_24))
        functions.add(Function(6, "Điều khoản sử dụng", "Điều khoản sử dụng", R.drawable.ic_baseline_security_24))
        functions.add(Function(7, "Chính sách bảo mật", "Chính sách bảo mật", R.drawable.ic_baseline_info_24))
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