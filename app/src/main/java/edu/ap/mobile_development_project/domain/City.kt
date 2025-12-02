package edu.ap.mobile_development_project.domain

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class City {
    var name: String = ""

    constructor()

    constructor(name: String) {
        this.name = name
    }
}