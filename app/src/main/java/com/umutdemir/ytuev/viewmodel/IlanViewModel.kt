package com.umutdemir.ytuev.viewmodel

import android.location.Geocoder
import android.net.Uri
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.umutdemir.ytuev.model.Ilan
import com.umutdemir.ytuev.model.Kullanici
import java.util.UUID

class IlanViewModel : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val currentUser = auth.currentUser!!
    val storage = Firebase.storage
    val reference = storage.reference
    private val ilanListesi = arrayListOf<Ilan>()
    private lateinit var ilanGuncel : Ilan


    fun getIlanList(onSuccess: (ArrayList<Ilan>?) -> Unit, onFailure: (Exception) -> Unit) {

        db.collection("Ilanlar").orderBy("tarih", Query.Direction.DESCENDING).get()
            .addOnSuccessListener { ilanList ->
                ilanListesi.clear()
                for (ilan in ilanList) {
                    ilanListesi.add(ilan.toObject(Ilan::class.java))
                }

                onSuccess(ilanListesi)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }

    }


    fun shareIlan(ilan: Ilan, callback: (Boolean) -> Unit) {
        db.collection("Ilanlar").add(ilan).addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

    fun fotografPaylas(uri: Uri,onSuccess: (String) -> Unit,onFailure: (Exception) -> Unit){
        var fotografUrl = ""
        val uuid = UUID.randomUUID()
        val imageName = "${uuid}.jpg"
        val imageReference = reference.child("fotograflar").child(imageName)
        imageReference.putFile(uri).addOnSuccessListener {
            val uploadedImageReference =
                Firebase.storage.reference.child("fotograflar")
                    .child(imageName)
            uploadedImageReference.downloadUrl.addOnSuccessListener {
                val downloadUrl = it.toString()
                fotografUrl = downloadUrl
                onSuccess(fotografUrl)
            }.addOnFailureListener{
                onFailure(it)
            }
        }
    }

}