package com.fathoor.storyapi.model.remote.retrofit

import com.fathoor.storyapi.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        var BASE_URL = BuildConfig.BASE_URL

        fun getApiService(): ApiService {
            val client = OkHttpClient.Builder()
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}