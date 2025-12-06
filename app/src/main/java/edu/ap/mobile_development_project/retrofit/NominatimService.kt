package edu.ap.mobile_development_project.retrofit

import edu.ap.osm.retrofit.Entry
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
interface NominatimService {
//    @Headers("User-Agent: Firefox/43.4")
//    @GET("search")
    //suspend fun getAddress(@Query("q") address: String, @Query("format") format: String): List<Entry>
//    fun getAddress(@Query("q") address: String, @Query("format") format: String): Call<List<Entry>>
    @GET("reverse")
    suspend fun getReverse(@Query("lat") lat: Double, @Query("lon") lon: Double, @Query("format") format: String): ReverseEntry
}