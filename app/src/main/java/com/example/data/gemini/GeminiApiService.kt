package com.example.data.gemini

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GeminiApiService {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContentWithModel(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse

    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

/**
 * Automatically retries transient server errors like HTTP 503 (Overloaded)
 * or HTTP 429 (Rate Limit) with exponential backoff before failing.
 */
class TransientRetryInterceptor(private val maxRetries: Int = 2) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        var response = chain.proceed(request)
        var attempts = 0
        while (!response.isSuccessful && response.code in listOf(503, 504, 429) && attempts < maxRetries) {
            attempts++
            response.close()
            try {
                Thread.sleep(600L * attempts)
            } catch (_: InterruptedException) {
                Thread.currentThread().interrupt()
                break
            }
            response = chain.proceed(request)
        }
        return response
    }
}

object GeminiRetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    private val okHttpClient: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(TransientRetryInterceptor(maxRetries = 2))
            .addInterceptor(logging)
            .build()
    }

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    val jsonMoshi: Moshi get() = moshi
}
