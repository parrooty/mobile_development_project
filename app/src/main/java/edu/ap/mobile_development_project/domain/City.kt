package edu.ap.mobile_development_project.domain

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class City {
    var id: String = ""
    var name: String = ""

    constructor()

    constructor(id: String, name: String) {
        this.id = id
        this.name = name
    }
}