package edu.ap.mobile_development_project.screens

import com.google.firebase.database.IgnoreExtraProperties
import edu.ap.mobile_development_project.domain.Rating
import edu.ap.mobile_development_project.enums.Category

@IgnoreExtraProperties
class PointOfInterest {
    var name: String = ""
    var lat: Double = .0
    var lon: Double = .0
    var image: String = ""
    var categories: List<Category> = listOf()
    var ratings: List<Rating> = listOf()
    var cityId: String = ""

    constructor()

    constructor(
        name: String,
        lat: Double,
        lon: Double,
        image: String,
        categories: List<Category>,
        ratings: List<Rating>,
        cityId: String) {
        this.name = name
        this.lat = lat
        this.lon = lon
        this.image = image
        this.categories = categories
        this.ratings = ratings
        this.cityId = cityId
    }
}