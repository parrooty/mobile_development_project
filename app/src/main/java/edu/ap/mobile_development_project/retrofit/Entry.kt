package edu.ap.osm.retrofit

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Entry (
    @SerializedName("lat") var lat : Double,
    @SerializedName("lon") var lon : Double
) : Serializable