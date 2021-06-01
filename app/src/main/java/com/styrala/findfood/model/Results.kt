package com.styrala.findfood.model

class Results {
    var geometry: Geometry? = null
    var icon: String? = null
    var name: String? = null
    var opening_hours: OpeningHours? = null
    var photos: Array<Photo>? = null
    var id: String? = null
    var place_id: String? = null
    var price_level = 0
    var rating = 0.0
    var reference: String? = null
    var scope: String? = null
    var types: Array<String>? = null
    var user_ratings_total = 0
    var vicinity: String? = null
    var reviews: List<Review>? = null
}