package edu.ap.mobile_development_project.domain

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Rating {
    var id: String = ""
    var rating: Int = 0
    var pointOfInterestId: String = ""
    var userId: String = ""

    constructor()

    constructor(rating: Int, pointOfInterestId: String, userId: String) {
        this.rating = rating
        this.pointOfInterestId = pointOfInterestId
        this.userId = userId
    }
}