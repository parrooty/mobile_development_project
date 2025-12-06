package edu.ap.mobile_development_project.retrofit

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ReverseEntry (
    @SerializedName("lat") var lat: Double,
    @SerializedName("lon") var lon: Double,
    @SerializedName("address") var address: Address
) : Serializable