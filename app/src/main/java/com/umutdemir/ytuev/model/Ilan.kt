package com.umutdemir.ytuev.model

import com.google.firebase.Timestamp

data class Ilan(
    var baslik : String = "",
    var il : String = "",
    var ilce : String = "",
    var paylasanIsim : String = "",
    var paylasanNumara : String = "",
    var fotograf : String = "",
    var aciklama : String = "",
    var mesafe : String = "",
    var sure : String = "",
    var fiyat : String = "",
    var tarih : Timestamp = Timestamp(0L,1)
)
