package com.umutdemir.ytuev.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.umutdemir.ytuev.viewmodel.ProfilViewModel
import com.umutdemir.ytuev.R
import com.umutdemir.ytuev.Util
import com.umutdemir.ytuev.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var viewModel: ProfilViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ProfilViewModel::class.java]

        when (intent.getStringExtra("id")) {
            "anasayfa"-> {
                loadFragment(AnasayfaFragment())
                binding.bottomNavigation.selectedItemId = R.id.menuAnasayfa
            }
            "harita"->{
                loadFragment(HaritaFragment())
                binding.bottomNavigation.selectedItemId = R.id.menuHarita
            }
            "profil"->{
                loadFragment(ProfilFragment())
                binding.bottomNavigation.selectedItemId = R.id.menuProfil
            }
            "ara"->{
                loadFragment(AraFragment())
                binding.bottomNavigation.selectedItemId = R.id.menuAra
            }

        }

        binding.bottomNavigation.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.menuProfil -> {
                    loadFragment(ProfilFragment())
                    true
                }
                R.id.menuHarita -> {
                    loadFragment(HaritaFragment())
                    true
                }
                R.id.menuAnasayfa -> {
                    loadFragment(AnasayfaFragment())
                    true
                }
                R.id.menuAra -> {
                    loadFragment(AraFragment())
                    true
                }
                R.id.menuMesajlar->{
                    loadFragment(GelenKutusuFragment())
                    true
                }

                else -> false
            }
        }


    }

    fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }
}