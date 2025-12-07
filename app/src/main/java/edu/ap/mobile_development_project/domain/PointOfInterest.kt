package edu.ap.mobile_development_project.domain

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties
import edu.ap.mobile_development_project.enums.Category

@IgnoreExtraProperties
class PointOfInterest {
    var id: String = ""
    var name: String = ""
    var description: String = ""
    var lat: Double = .0
    var lon: Double = .0
    var image: String = ""
    var categories: List<Category> = listOf()
    var cityId: String = ""

    @get:Exclude
    var ratings: List<Rating> = emptyList()
    @get:Exclude
    var comments: List<Comment> = emptyList()

    constructor()

    constructor(
        id: String,
        name: String,
        description: String,
        lat: Double,
        lon: Double,
        image: String,
        categories: List<Category>,
        cityId: String,
        ratings: List<Rating>,
        comments: List<Comment>
    ) {
        this.id = id
        this.name = name
        this.description = description
        this.lat = lat
        this.lon = lon
        this.image = image
        this.categories = categories
        this.cityId = cityId
        this.ratings = ratings
        this.comments = comments
    }

    constructor(
        name: String,
        description: String,
        lat: Double,
        lon: Double,
        image: String,
        categories: List<Category>,
        cityId: String
    ) {
        this.name = name
        this.description = description
        this.lat = lat
        this.lon = lon
        this.image = image
        this.categories = categories
        this.cityId = cityId
    }

    fun copy(
        id: String = this.id,
        name: String = this.name,
        description: String = this.description,
        lat: Double = this.lat,
        lon: Double = this.lon,
        image: String = this.image,
        categories: List<Category> = this.categories,
        cityId: String = this.cityId,
        ratings: List<Rating> = this.ratings,
        comments: List<Comment> = this.comments
    ): PointOfInterest {
        return PointOfInterest(id, name, description, lat, lon, image, categories, cityId, ratings, comments)
    }
}