package com.styrala.findfood.model

class Results {
    var geometry: Geometry? = null
    var icon: String? = null
    var name: String? = null
    var openingHours: OpeningHours? = null
    var photos: Array<Photo>? = null
    var id: String? = null
    var placeId: String? = null
    var priceLevel = 0
    var rating = 0.0
    var reference: String? = null
    var scope: String? = null
    var types: Array<String>? = null
    var userRatingsTotal = 0
    var vicinity: String? = null
}