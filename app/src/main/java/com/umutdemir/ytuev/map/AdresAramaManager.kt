package com.umutdemir.ytuev.map


import android.content.Context
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.umutdemir.ytuev.R
import com.umutdemir.ytuev.map.data.model.Adres

class AdresAramaManager(private val context: Context) {
    private val placesClient: PlacesClient

    init {
        // Places API istemcisini başlat
        Places.initialize(context, "AIzaSyDv6JfgLfYlRhVcvJqYSDiRX30p2p2mRj0")
        placesClient = Places.createClient(context)
    }

    fun aramaYap(
        aramaMetni: String,
        onsucces: (ArrayList<Adres>?) -> Unit,
        onfailure: (Exception) -> Unit
    ) {
        // Adres arama isteğini oluştur
        val placesClient = Places.createClient(context)
        val token = AutocompleteSessionToken.newInstance()

        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(aramaMetni)
            .setCountry("TR") // Arama sonuçlarını Türkiye ile sınırla
            .setLocationBias(
                RectangularBounds.newInstance(
                    LatLng(41.0, 26.0),
                    LatLng(42.0, 29.0)
                )
            ) // Bölge sınırlarını belirt
            .build()

        // Adres araması yap ve sonuçları işle
        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                val predictions = response.autocompletePredictions
                val adresler = arrayListOf<Adres>()

                if(predictions.isNullOrEmpty()){
                    onsucces(arrayListOf())
                }
                else{
                    for (prediction in predictions) {
                        val adresId = prediction.placeId
                        val adresIsim = prediction.getPrimaryText(null).toString()

                        placesClient.fetchPlace(
                            FetchPlaceRequest.builder(
                                adresId,
                                listOf(Place.Field.ADDRESS, Place.Field.LAT_LNG)
                            ).build()
                        ).addOnSuccessListener {fetchResponse->
                            val place = fetchResponse.place
                            val konum = place.latLng!!
                            val isim = place.address!!
                            val adres = Adres(isim,konum)
                            adresler.add(adres)
                            onsucces(adresler)
                        }
                    }
                }
                println("fonksiyon ici adresler : $adresler")

            }
            .addOnFailureListener { exception ->
                // Adres araması sırasında hata oluştu
               onfailure(exception)
            }
    }


}