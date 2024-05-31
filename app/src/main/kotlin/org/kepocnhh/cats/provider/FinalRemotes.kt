package org.kepocnhh.cats.provider

import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

internal class FinalRemotes : Remotes {
    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .build()

    override fun getRandomCat(): ByteArray {
        val request = Request.Builder()
            .url("https://cataas.com/cat")
            .build()
        return client.newCall(request).execute().use {
            when (it.code) {
                200 -> it.body!!.bytes()
                else -> error("Unknown code ${it.code}!")
            }
        }
    }
}
