package com.umutdemir.ytuev.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.umutdemir.ytuev.model.Ilan
import com.umutdemir.ytuev.model.Kullanici

class ProfilViewModel : ViewModel() {


    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val currentUser = auth.currentUser!!
    private var kullanici: Kullanici? = null

    fun getUserData(onSuccess: (Kullanici?) -> Unit, onFailure: (Exception) -> Unit) {
            // Kullanıcı verileri daha önce alınmamışsa, Firestore'dan verileri al
            db.collection("Kullanicilar").document(currentUser.uid).get()
                .addOnSuccessListener { documentSnapshot ->
                    kullanici = documentSnapshot.toObject(Kullanici::class.java)
                    onSuccess(kullanici)
                }
                .addOnFailureListener { exception ->
                    onFailure(exception)
                }

    }

    fun setUserData(kullanici: Kullanici, callback: (Boolean) -> Unit) {
        db.collection("Kullanicilar").document(currentUser.uid).update(
            "fotograf",kullanici.fotograf,
            "durum", kullanici.durum,
            "isim", kullanici.isim,
            "numara", kullanici.numara,
            "mail", kullanici.mail,
            "il", kullanici.il,
            "ilce", kullanici.ilce,
            "hakkinda", kullanici.hakkinda,
            "bolum", kullanici.bolum,
            "uzaklik", kullanici.uzaklik,
            "sure", kullanici.sure,
            "confirmMail", kullanici.confirmMail,
            "sinif", kullanici.sinif,
        ).addOnSuccessListener {
         callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

    fun getIlan(kullanici: Kullanici,onSuccess: (Ilan) -> Unit) {
        val ilanListesi = arrayListOf<Ilan>()
        println(kullanici.mail)
        db.collection("Ilanlar").whereEqualTo("paylasanMail",kullanici.mail).get()
            .addOnSuccessListener { ilanList ->
                ilanListesi.clear()
                for (ilan in ilanList) {
                    ilanListesi.add(ilan.toObject(Ilan::class.java))
                }
                val ilan = ilanListesi[0]
                println("ilan : $ilan")
                onSuccess(ilan)
            }


    }


}