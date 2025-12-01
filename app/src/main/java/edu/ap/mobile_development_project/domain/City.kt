package edu.ap.mobile_development_project.domain

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class City {
    var name: String = ""
    var lat: Double = .0
    var lon: Double = .0

    constructor()

    constructor(name: String, lat: Double, lon: Double) {
        this.name = name
        this.lat = lat
        this.lon = lon
    }
}