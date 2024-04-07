package com.galaxy.galaxynet.notification

import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

object PushNotificationService {
    suspend fun sendNotificationToDevice(deviceToken: String, title: String, message: String) {
        val url = "https://fcm.googleapis.com/fcm/send"
        val mediaType = com.galaxy.util.Constants.CONTENT_TYPE.toMediaType()

        val json = """
        {
                "to": "$deviceToken",
            "notification": {
                "title": "$title",
                "body": "$message"
            }
        }
    """.trimIndent()

        val requestBody = json.toRequestBody(mediaType)
        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Key=${com.galaxy.util.Constants.SERVER_KEY}")
            .addHeader("Content-Type", com.galaxy.util.Constants.CONTENT_TYPE)
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val response = response
                Log.d("FCM Notification", "Notification sent successfully$response")
            }

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                val response = e.localizedMessage
                Log.e("FCM Notification", "Failed to send notification: $e")
            }
        })
    }
}