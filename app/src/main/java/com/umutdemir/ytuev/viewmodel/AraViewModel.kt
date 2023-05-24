package com.umutdemir.ytuev.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.umutdemir.ytuev.model.Kullanici

class AraViewModel : ViewModel() {


    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val currentUser = auth.currentUser!!
    private val kullaniciListesi = arrayListOf<Kullanici>()

    fun getUserList(onSuccess: (ArrayList<Kullanici>?) -> Unit, onFailure: (Exception) -> Unit) {

        // Kullanıcı verileri daha önce alınmamışsa, Firestore'dan verileri al
        db.collection("Kullanicilar").get()
            .addOnSuccessListener { kullaniciList ->
                kullaniciListesi.clear()
                for (kullanici in kullaniciList) {
                    kullaniciListesi.add(kullanici.toObject(Kullanici::class.java))
                }

                onSuccess(kullaniciListesi)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }

    }
}