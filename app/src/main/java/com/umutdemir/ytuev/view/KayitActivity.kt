package com.umutdemir.ytuev.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.umutdemir.ytuev.model.Kullanici
import com.umutdemir.ytuev.R
import com.umutdemir.ytuev.databinding.ActivityKayitBinding

class KayitActivity : AppCompatActivity() {
    private lateinit var binding: ActivityKayitBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKayitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.geriButon.setOnClickListener {
            val intent = Intent(this, GirisActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.button.setOnClickListener {
            boslukKontrol()
        }
    }

    fun boslukKontrol() {
        var ok = 1
        val editTexts = arrayOf(
            binding.nameEditText,
            binding.mailEditText,
            binding.passwordEditText,
            binding.passwordConfirmEditText,

            )
        val textLayouts = arrayOf(
            binding.nameTextLayout,
            binding.mailTextLayout,
            binding.passwordTextLayout,
            binding.passwordConfirmTextLayout,

            )
        for (i in editTexts.indices) {
            if (editTexts[i].text.toString().isEmpty()) {
                textLayouts[i].helperText = getString(R.string.required)
                ok = 0
            } else {
                textLayouts[i].helperText = null
            }
        }
        if (ok == 1) {
            if (degerKontrol()) {
                kaydol()
            }
        }
    }

    fun degerKontrol(): Boolean {
        var ok = true
        if (!binding.mailEditText.text!!.contains("std.yildiz.edu.tr")) {
            binding.mailTextLayout.helperText =
                getString(R.string.emailnotvalid)
            ok = false
        }
        if (binding.passwordEditText.text.toString() != binding.passwordConfirmEditText.text.toString()) {
            binding.passwordTextLayout.helperText =
                getString(R.string.password_problem)
            binding.passwordConfirmTextLayout.helperText =
                getString(R.string.password_problem)
            ok = false
        } else if (!isValidPassword(binding.passwordEditText.text.toString())) {
            binding.passwordTextLayout.helperText =
                "Lütfen güçlü bir parola girin(En az 6 karakter,Büyük harf,Küçük harf,Sayı,Özel karakter içermeli)"
            ok = false
        }
        return ok
    }

    fun isValidPassword(password: String?): Boolean {
        password?.let {
            if (it.length < 6) {
                return false
            }
            val passwordPattern =
                "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+=-])(?=\\S+$).{4,}$"
            val passwordMatcher = Regex(passwordPattern)

            return passwordMatcher.find(password) != null
        } ?: return false
    }

    fun kaydol() {
        println("mail : ${binding.mailEditText.text.toString()}")
        println("sifre : ${binding.passwordEditText.text.toString()}")

        auth.createUserWithEmailAndPassword(
            binding.mailEditText.text.toString(),
            binding.passwordEditText.text.toString()
        )
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    val db = Firebase.firestore
                    val kullanici = Kullanici()
                    kullanici.isim = binding.nameEditText.text.toString()
                    kullanici.mail = binding.mailEditText.text.toString()
                    db.collection("Kullanicilar")
                        .document(user!!.uid).set(kullanici).addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(
                                    this, getString(R.string.successfulRegistration),
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(this, GirisActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }.addOnFailureListener { e ->
                            println("db yukleme : ${e.message}")
                        }


                } else {
                    // If sign in fails, display a message to the user.
                    println("createUserWithEmail:failure : ${task.exception}")
                    Toast.makeText(
                        baseContext,
                        "Kayit Hatasi",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }
}