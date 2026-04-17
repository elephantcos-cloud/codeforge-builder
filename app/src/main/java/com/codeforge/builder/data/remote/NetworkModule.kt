package com.codeforge.builder.data.remote

import com.codeforge.builder.utils.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkModule {

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Accept", Constants.GITHUB_ACCEPT_HEADER)
                .addHeader("X-GitHub-Api-Version", Constants.GITHUB_API_VERSION)
                .build()
            chain.proceed(request)
        }
        .connectTimeout(Constants.NETWORK_CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(Constants.NETWORK_READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(Constants.NETWORK_WRITE_TIMEOUT, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.GITHUB_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: GitHubApiService = retrofit.create(GitHubApiService::class.java)

    // Download client (follows redirects, longer timeout)
    val downloadClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .followRedirects(true)
        .followSslRedirects(true)
        .build()
}
