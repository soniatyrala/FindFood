package com.styrala.findfood.model

class Review {
    var id: String? = null
    var author_name: String? = null
    var author_url: String? = null
    var language: String? = null
    var profile_photo_url: String? = null
    var rating = 0.0
    var relative_time_description: String? = null
    var text: String? = null
    var time: String? = null
    var place_id: String? = null

    constructor()

    constructor(id: String, rating: Double, text: String?, time: String, place_id: String) {
        this.id = id
        this.rating = rating
        this.text = text
        this.time = time
        this.place_id = place_id
    }
}