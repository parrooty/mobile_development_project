package edu.ap.mobile_development_project.domain

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class City(val name: String, val lat: Double, val lon: Double) {
}