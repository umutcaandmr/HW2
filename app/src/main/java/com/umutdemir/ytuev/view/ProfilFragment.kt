package com.umutdemir.ytuev.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.umutdemir.ytuev.viewmodel.ProfilViewModel
import com.umutdemir.ytuev.R
import com.umutdemir.ytuev.databinding.FragmentProfilBinding
import com.umutdemir.ytuev.model.Kullanici


class ProfilFragment : Fragment() {

    private lateinit var binding: FragmentProfilBinding
    private lateinit var viewModel: ProfilViewModel
    private lateinit var kullanici: Kullanici

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfilBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val auth = Firebase.auth
        val currentUser = auth.currentUser
        viewModel = ViewModelProvider(requireActivity())[ProfilViewModel::class.java]


        getUserData()

        binding.menu.setOnClickListener {
            //Toast.makeText(requireContext(),"tiklandi",Toast.LENGTH_SHORT).show()
            showPopup(it,auth)
        }



    }

    override fun onResume() {
        super.onResume()
        getUserData()
    }

    private fun getUserData(){
        viewModel.getUserData(onSuccess = {kullanici ->

            kullanici?.let {
                if(kullanici.fotograf.isNotEmpty()){
                    Picasso.get().load(kullanici.fotograf).into(binding.profilFotografi)
                }
                when(kullanici.durum){
                    "Ev Arıyorum"->{
                        binding.profilDurum.setTextColor(Color.GREEN)
                        binding.profilDurum.text = context?.getString(R.string.ev_arama)
                    }
                    "Ev Arkadaşı Arıyorum"->{
                        binding.profilDurum.setTextColor(Color.GREEN)
                        binding.profilDurum.text = context?.getString(R.string.ev_arkadas_arama)
                    }
                    else->{
                        binding.profilDurum.setTextColor(Color.RED)
                        binding.profilDurum.text = context?.getString(R.string.dolu)
                    }
                }
                val kullaniciIsim = kullanici.isim
                binding.profilIsim.text = kullaniciIsim
                binding.profilMail.text = kullanici.mail
                val kullaniciKonum = kullanici.ilce +  " / " + kullanici.il
                binding.profilKonum.text = kullaniciKonum
                binding.profilHakkinda.text = kullanici.hakkinda
                binding.bolum.text = kullanici.bolum + " / " + kullanici.sinif
                binding.profilSure.text = kullanici.sure + " ay"
                binding.profilTelefon.text = kullanici.numara
                binding.profilUzaklik.text = kullanici.uzaklik + " km"
                binding.progressBar.visibility = View.INVISIBLE
                binding.layout.visibility = View.VISIBLE

            }

        }, onFailure = {exception ->

        })
    }


    private fun showPopup(view: View, auth : FirebaseAuth ) {
        val popupMenu = PopupMenu(requireContext(),view)
        popupMenu.inflate(R.menu.profil_ayarlar)
        popupMenu.setOnMenuItemClickListener {

            when(it.itemId){
                R.id.menuDuzenle ->{
                   val activity = requireActivity() as MainActivity
                    activity.loadFragment(ProfilDuzenleFragment())
                    true

                }
                R.id.menuCikis ->{
                    auth.signOut()
                    val intent = Intent(requireContext(),GirisActivity::class.java)
                    startActivity(intent)
                    val activity = requireActivity() as MainActivity
                    activity.finish()
                    true
                }
                else -> true
            }
        }

        popupMenu.show()

    }



}