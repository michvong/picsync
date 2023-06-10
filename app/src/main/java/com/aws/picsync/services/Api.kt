package com.aws.picsync.services

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException

class Api {
    private val client = OkHttpClient()

    fun createBucket() {
        val request: Request = Request.Builder()
            .url("http://10.0.2.2:5000/create-bucket")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body.string()
                    println(responseBody)
                } else {
                    println("Unable to create bucket")
                }
            }
        })
    }

    fun uploadFile(imageFile: File) {
        val requestBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                imageFile.name,
                imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            )
            .build()

        val request: Request = Request.Builder()
            .url("http://10.0.2.2:5000/upload-file")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val responseBody = response.body.string()
                    println(responseBody)
                } else {
                    println("Unable to upload image")
                }
            }
        })
    }
}