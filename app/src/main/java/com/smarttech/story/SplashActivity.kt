package com.smarttech.story

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.view.View
import android.widget.Toast
import com.smarttech.story.constants.Constants
import com.smarttech.story.database.AppDatabase
import com.smarttech.story.networking.DropboxService
import com.smarttech.story.utils.FileUtil
import com.smarttech.story.utils.UnzipUtility
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import kotlin.coroutines.CoroutineContext

class SplashActivity : AppCompatActivity(), CoroutineScope {
    val activityScope = CoroutineScope(Dispatchers.Main)

    //https://www.programmersought.com/article/37335883860/
    //job is used to control the coroutine
    private lateinit var job: Job

    //Inheriting CoroutineScope must initialize the coroutineContext variable
    // This is the standard way of writing, + is actually the plus method, which means job in the front, used to control the coroutine, and Dispatchers in the back, specifying the thread to start
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Initialize job in onCreate
        job = Job()
        //hideSystemUI();
        setContentView(R.layout.activity_splash)
        val threadPolicy = StrictMode.ThreadPolicy.Builder()
            .permitDiskReads()
            .permitDiskWrites() // If you also want to ignore DiskWrites, Set this line too.
            .build();
        if (!FileUtil.databaseFileExists(this, "story.db")) {

            activityScope.launch {
                //Start directly from the IO thread here
                launch(Dispatchers.IO) {
                    val result = FileUtil.attachDbFromDropBox(applicationContext, "uczd5fxz2c74lfn", "story")
                    if (result) {
                        var intent = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        withContext(Main) {
                            Toast.makeText(applicationContext, "Có lỗi xẩy ra, vui lòng kiểm tra kết nối mạng và chạy lại...", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }
            }
        } else {
            var intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    override fun onPause() {
        activityScope.cancel()
        super.onPause()
    }
}