package com.umutdemir.ytuev

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng


object Util {
    fun konumBul(context : Activity,callback: (LatLng?) -> Unit) {
         val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)
        val task = fusedLocationClient.lastLocation
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Konum izni verilmemiş, izin isteği göster
            ActivityCompat.requestPermissions(
               context , arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 0
            )
        }
        task.addOnSuccessListener { location ->
            if (location != null) {
                val enlem = location.latitude
                val boylam = location.longitude

                callback(LatLng(enlem,boylam))
            }
        }.addOnFailureListener {

            Toast.makeText(context, "Konum bulunamadı. Lutfen konum ayarlarini kontrol edin", Toast.LENGTH_SHORT).show()
        }
    }

}





