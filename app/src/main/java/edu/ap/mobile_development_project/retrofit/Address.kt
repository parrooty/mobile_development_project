package edu.ap.mobile_development_project.retrofit

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Address (
    @SerializedName("city") var city: String?,
    @SerializedName("town") var town: String?,
    @SerializedName("country") var country: String
) : Serializable