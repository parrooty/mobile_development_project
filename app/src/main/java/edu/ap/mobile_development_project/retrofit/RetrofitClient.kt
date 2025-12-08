package edu.ap.osm.retrofit

import edu.ap.mobile_development_project.retrofit.NominatimService
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://nominatim.openstreetmap.org/"

    // If you want to log all http traffic
    val interceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val userAgentInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()

        val requestWithUserAgent = originalRequest.newBuilder()
            .header("User-Agent", "MobileDevelopmentProject/1.0 (edu.ap.mobile_development_project)")
            .build()

        chain.proceed(requestWithUserAgent)
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .addInterceptor(userAgentInterceptor)
        .build()

    val instance: NominatimService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NominatimService::class.java)
    }
}