package com.example.bookmyshowtask.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitInstance {

    companion object {
        private var baseUrl = ""
        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        /*val api by lazy {
            retrofit.create(DtmsEtmsAPI::class.java)
        }*/
        fun api(baseUrl: String): TestAPI {
            this.baseUrl = baseUrl
            return retrofit.create(TestAPI::class.java)
        }
    }
}