package edu.ap.mobile_development_project.domain

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class Comment {
    var id: String = ""
    var comment: String = ""
    var pointOfInterestId: String = ""
    var userId: String = ""

    constructor()

    constructor(id: String, comment: String, pointOfInterestId: String, userId: String) {
        this.id = id
        this.comment = comment
        this.pointOfInterestId = pointOfInterestId
        this.userId = userId
    }

    constructor(comment: String, pointOfInterestId: String, userId: String) {
        this.comment = comment
        this.pointOfInterestId = pointOfInterestId
        this.userId = userId
    }

    fun copy(
        id: String = this.id,
        comment: String = this.comment,
        pointOfInterestId: String = this.pointOfInterestId,
        userId: String = this.userId
    ): Comment {
        return Comment(id, comment, pointOfInterestId, userId)
    }
}