package com.umutdemir.ytuev.view

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.umutdemir.ytuev.R
import com.umutdemir.ytuev.Util
import com.umutdemir.ytuev.databinding.FragmentIlanEkleBinding
import com.umutdemir.ytuev.map.AdresAramaManager
import com.umutdemir.ytuev.map.data.model.Adres
import com.umutdemir.ytuev.model.Ilan
import com.umutdemir.ytuev.viewmodel.IlanViewModel
import com.umutdemir.ytuev.viewmodel.ProfilViewModel
import java.util.Locale


class IlanEkleFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private lateinit var binding: FragmentIlanEkleBinding
    private lateinit var ilanViewModel: IlanViewModel
    private lateinit var profilViewModel: ProfilViewModel
    private lateinit var selectedUri: Uri
    private lateinit var selectedBitmap: Bitmap
    private lateinit var adresAramaManager: AdresAramaManager
    private var fotografUrl: String = ""
    private var uzaklik = -1
    private var adresler = arrayListOf<Adres>()
    private var adres: Adres? = null
    private lateinit var kullaniciKonum: LatLng
    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            selectedUri = uri
            val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
            selectedBitmap = ImageDecoder.decodeBitmap(source)
            binding.ilanFotograf.setImageBitmap(selectedBitmap)

        } else {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentIlanEkleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        ilanViewModel = ViewModelProvider(this)[IlanViewModel::class.java]
        profilViewModel = ViewModelProvider(this)[ProfilViewModel::class.java]
        adresAramaManager = AdresAramaManager(requireContext())
        val adapter = ArrayAdapter(requireContext(), R.layout.adres_list_row, arrayListOf<String>())
        binding.adreslistView.adapter = adapter
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapIlan) as SupportMapFragment?

        Util.konumBul(requireActivity(), callback = { konum ->
            if (konum != null) {
                kullaniciKonum = konum
                mapFragment!!.getMapAsync {
                    setupMap(it)
                }
            }
        })



        binding.ilanFotograf.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                println(newText)
                if (newText.isNullOrEmpty()) {
                    adapter.clear()
                    adapter.addAll(arrayListOf())
                    adapter.notifyDataSetChanged()
                } else {
                    adresAramaManager.aramaYap(newText, onsucces = {
                        if (it.isNullOrEmpty()) {
                            adapter.clear()
                            adapter.addAll(arrayListOf())
                            adapter.notifyDataSetChanged()
                        } else {
                            adresler = it
                            println(adresler)
                            val isimListesi =
                                adresler.map { adres -> adres.isim } as ArrayList<String>
                            println(isimListesi)
                            adapter.clear()
                            adapter.addAll(isimListesi)
                            adapter.notifyDataSetChanged()
                        }
                    }, onfailure = {})
                }
                return true
            }

        })

        binding.adreslistView.setOnItemClickListener { parent, view, position, id ->
            adapter.clear()
            adapter.addAll(arrayListOf())
            adapter.notifyDataSetChanged()
            adres = adresler[position]
            mapFragment!!.getMapAsync {
                setupMap(it)
            }
        }

        binding.paylasButon.setOnClickListener {
            ilanViewModel.fotografPaylas(selectedUri, onSuccess = { url ->
                fotografUrl = url
                Toast.makeText(requireContext(), fotografUrl, Toast.LENGTH_SHORT).show()
                profilViewModel.getUserData(onSuccess = { kullanici ->
                    if (kullanici != null) {
                        val ilan = Ilan(
                            baslik = binding.ilanBaslik.text.toString(),
                            il = binding.ilanIl.text.toString(),
                            ilce = binding.ilanIlce.text.toString(),
                            paylasanIsim = kullanici.isim,
                            paylasanNumara = kullanici.numara,
                            fotograf = fotografUrl,
                            aciklama = binding.ilanAciklama.text.toString(),
                            mesafe = uzaklik.toString(),
                            sure = binding.ilanSure.text.toString(),
                            fiyat = binding.ilanFiyat.text.toString(),
                            tarih = com.google.firebase.Timestamp.now()
                        )
                        ilanViewModel.shareIlan(ilan, callback = {
                            if (it) {
                                Toast.makeText(requireContext(), "tamam", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(requireContext(), "hata", Toast.LENGTH_SHORT).show()

                            }
                        })
                    }
                }, onFailure = { e ->

                })

            }, onFailure = {
                Toast.makeText(requireContext(), "url alinamadi", Toast.LENGTH_SHORT).show()

            })


        }

    }

    override fun onMapReady(p0: GoogleMap) {
        setupMap(p0)
    }

    override fun onMarkerDrag(p0: Marker) {

    }

    override fun onMarkerDragEnd(p0: Marker) {
        Toast.makeText(requireContext(), "deneme", Toast.LENGTH_SHORT).show()
        val konum = p0.position
        Toast.makeText(requireContext(), konum.latitude.toString(), Toast.LENGTH_SHORT).show()
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(konum.latitude, konum.longitude, 1
            ) { addresses ->
                adres = Adres(
                    addresses[0].featureName,
                    LatLng(addresses[0].latitude, addresses[0].longitude)
                )
                binding.ilanIlce.setText(addresses[0].subLocality ?: addresses[0].adminArea)
                binding.ilanIl.setText(addresses[0].locality ?: addresses[0].subAdminArea)
                binding.searchView.setQuery("", false)
                binding.searchView.clearFocus()
                binding.searchView.queryHint = addresses[0].getAddressLine(0)
            }
        }
    }


    override fun onMarkerDragStart(p0: Marker) {

    }

    private fun setupMap(googleMap: GoogleMap) {

        googleMap.clear()

        if (adres == null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(kullaniciKonum, 12f))
        } else {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(adres!!.konum, 12f))
            googleMap.addMarker(MarkerOptions().position(adres!!.konum).draggable(true))
            googleMap.setOnMarkerDragListener(this)
        }
        googleMap.isMyLocationEnabled = true
    }
}