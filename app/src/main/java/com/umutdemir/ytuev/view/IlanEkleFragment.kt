package com.umutdemir.ytuev.view

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
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
    private lateinit var  mapFragment : SupportMapFragment
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
        val ilanKonum = LatLng(0.0,0.0)
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
            val konum = adres!!.konum
            val results = FloatArray(1)
            Location.distanceBetween(
                41.021180043627695, 28.898643334027827,
                konum.latitude, konum.longitude,
                results
            )
            val mesafe = String.format("%.1f km", results[0] / 1000)
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
                            paylasanMail = kullanici.mail,
                            fotograf = fotografUrl,
                            aciklama = binding.ilanAciklama.text.toString(),
                            mesafe = mesafe,
                            sure = binding.ilanSure.text.toString(),
                            fiyat = binding.ilanFiyat.text.toString(),
                            konumEnlem = adres!!.konum.latitude,
                            konumBoylam = adres!!.konum.longitude,
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mapFragment.getMapAsync {
            setupMap(it)
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
        val results = FloatArray(1)
        Location.distanceBetween(
            41.021180043627695, 28.898643334027827,
            konum.latitude, konum.longitude,
            results
        )
        val km = String.format("%.1f", results[0] / 1000)
        Toast.makeText(requireContext(),"$km km", Toast.LENGTH_SHORT).show()
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