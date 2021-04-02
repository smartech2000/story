package com.smarttech.story.utils

import android.content.Context
import android.content.Intent
import com.smarttech.story.MainActivity
import com.smarttech.story.constants.Constants
import com.smarttech.story.networking.DropboxService
import java.io.*

class FileUtil {
    companion object {
        fun databaseFileExists(context: Context, dbName: String): Boolean {
            return try {
                File(context.getDatabasePath(dbName).absolutePath).exists()
            } catch (e: Exception) {
                false
            }
        }

        fun loadFromUrlToBytes(url: String): ByteArray? {
            val response = DropboxService.getInstance().downlload(url).execute()
            val body = response.body()
            if (body == null) {
                return null
            }
            val ins: InputStream = body.byteStream()
            val input = BufferedInputStream(ins)
            val output: ByteArrayOutputStream = ByteArrayOutputStream()

            val data = ByteArray(1024)

            var count = 0
            while (input.read(data).also({ count = it }) !== -1) {
                output.write(data, 0, count)
            }

            output.flush()
            output.close()
            input.close()

            return output.toByteArray()
        }

        fun attachDbFromDropBox(context: Context, shareKey: String, fileName: String): Boolean {
            var result = false
            var inStream: InputStream? = null
            var outStream: OutputStream? = null

            try {
                val url = Constants.DROPBOX_URL.replace("{shareKey}", shareKey)
                    .replace("{fileName}", fileName + ".db.zip")
                val response = DropboxService.getInstance().downlload(url).execute()
                val body = response.body()
                if (response.isSuccessful && body != null) {

                    /*Note that there are no checked exceptions in Kotlin,
                                     If try catch is not written here, the compiler will not report an error.
                                     But we need to ensure that the stream is closed, so we need to operate finally*/
                    //The following operations to read and write files are similar to java
                    inStream = body.byteStream()
                    //outStream = file.outputStream()
                    outStream = context.openFileOutput(fileName + ".zip", Context.MODE_PRIVATE)
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

                        }

                        len = inStream.read(buff)
                    }

                }
                result = true
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                inStream?.close()
                outStream?.close()

                val unzip = UnzipUtility()
                context.deleteDatabase(fileName + ".db")
                UnzipUtility.unzip(
                    context.getFileStreamPath(fileName + ".zip").path,
                    context.getFileStreamPath(fileName + ".zip").parentFile.parent + File.separator + "databases"
                )
                context.getFileStreamPath(fileName + ".zip").delete()

            }
            return result

        }
    }
}