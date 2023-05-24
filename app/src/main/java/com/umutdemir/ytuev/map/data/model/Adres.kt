package com.umutdemir.ytuev.map.data.model

import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place

data class Adres(
    var isim : String,
    var konum : LatLng
)
