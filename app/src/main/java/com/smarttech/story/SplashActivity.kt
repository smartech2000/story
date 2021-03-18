package com.smarttech.story

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import com.smarttech.story.database.AppDatabase
import com.smarttech.story.networking.DropboxService
import com.smarttech.story.utils.UnzipUtility
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.*
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
        hideSystemUI();
        setContentView(R.layout.activity_splash)

        if (!databaseFileExists()) {

            activityScope.launch {
                //delay(3000)

                //Start directly from the IO thread here
                launch(Dispatchers.IO) {
                    //Switch back to the IO thread to create the downloaded file
                    val url = "https://www.dropbox.com/s/fb5m5sdillbs4my/story.db.zip?dl=1"
                    val response = DropboxService.getInstance().downlload(url).execute()
                    val body = response.body()
                    if (response.isSuccessful && body != null) {
                        var inStream: InputStream? = null
                        var outStream: OutputStream? = null
                        /*Note that there are no checked exceptions in Kotlin,
                                         If try catch is not written here, the compiler will not report an error.
                                         But we need to ensure that the stream is closed, so we need to operate finally*/
                        try {
                            //The following operations to read and write files are similar to java
                            inStream = body.byteStream()
                            //outStream = file.outputStream()
                            outStream = openFileOutput("story.zip", Context.MODE_PRIVATE)
                            //Total file length
                            val contentLength = body.contentLength()
                            //Currently downloaded length
                            var currentLength = 0L
                            //Buffer
                            val buff = ByteArray(1024)
                            var len = inStream.read(buff)
                            var percent = 0
                            while (len != -1) {
                                outStream.write(buff, 0, len)
                                currentLength += len
                                /*Don't call the switch thread frequently, otherwise some mobile phones may cause stalls due to frequent thread switching.
                             Here is a restriction. Only when the download percentage is updated, will the thread be switched to update the UI*/
                                if ((currentLength * 100 / contentLength).toInt() > percent) {
                                    percent = (currentLength / contentLength * 100).toInt()
                                    //Switch to the main thread to update the UI
                                    /*                    withContext(Dispatchers.Main) {
                                    tv_download_state.text = "downloading:$currentLength / $contentLength"
                                }*/
                                    //Switch back to the IO thread immediately after updating the UI
                                }

                                len = inStream.read(buff)
                            }
/*                        //After the download is complete, switch to the main thread to update the UI
                        withContext(Dispatchers.Main) {
                            tv_download_state.text = "Download completed"
                            btn_down.visibility = View.VISIBLE
                        }*/

                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            inStream?.close()
                            outStream?.close()

                            val unzip = UnzipUtility()
                            applicationContext.deleteDatabase("story.db")
                            UnzipUtility.unzip(
                                getFileStreamPath("story.zip").path,
                                getFileStreamPath("story.zip").parentFile.parent + File.separator + "databases"
                            )
                            getFileStreamPath("story.zip").delete()
/*                            //After the download is complete, switch to the main thread to update the UI
                            withContext(Dispatchers.Main) {
                                progressBar.visibility =View.GONE
                            }*/
                            var intent = Intent(this@SplashActivity, MainActivity::class.java)
                            startActivity(intent)
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
    private fun databaseFileExists(): Boolean {
        return try {
            File(getDatabasePath("story.db").absolutePath).exists()
        }catch (e: Exception){
            false
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

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}