package com.styrala.findfood.model

class ResultsDetails {
    var address_components: Array<AddressComponent>? = null
    var adr_address: String? = null
    var business_status: String? = null
    var formatted_address: String? = null
    var geometry: Geometry? = null
    var icon: String? = null
    var name: String? = null
    var photos: List<Photo>? = null
    var place_id: String? = null
    var plus_code: PlusCode? = null
    var rating = 0.0
    var reference: String? = null
    var reviews: List<Review>? = null
    var types: List<String>? = null
    var url: String? = null
    var user_ratings_total = 0
    var utc_offset = 0
    var vicinity: String? = null
}
