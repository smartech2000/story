package com.smarttech.story.networking

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface DropboxService {

    @Streaming
    @GET
    fun downlload(@Url fileUrl: String): Call<ResponseBody>

    companion object {
        //Volatile annotation is the same as the volatile keyword in java, indicating that the attribute is immediately visible in other threads after the attribute is updated
        @Volatile
        private var instance:DropboxService? = null
        /*The "?:" here is an Elvis expression, meaning that the previous one is not null, and the previous one is directly returned.
         If the previous one is null, perform the following operation, which is used twice here, which is equivalent to double-check*/
        fun getInstance(): DropboxService = instance?: synchronized(DropboxService::class.java){
            instance?:Retrofit.Builder()
                .baseUrl("https://www.dropbox.com/")
                .build()
                .create(DropboxService::class.java)
                .also { instance = it }
        }
    }
}