package com.umutdemir.ytuev.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.umutdemir.ytuev.R
import com.umutdemir.ytuev.Util
import com.umutdemir.ytuev.databinding.FragmentHaritaBinding

class HaritaFragment : Fragment() {

    private lateinit var binding: FragmentHaritaBinding
    private lateinit var kullaniciKonum : LatLng
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHaritaBinding.inflate(layoutInflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapHarita) as SupportMapFragment?

        Util.konumBul(requireActivity(), callback = {konum->
            if(konum!=null){
                kullaniciKonum = konum
               mapFragment?.getMapAsync(callback)
            }
        })



    }


    private val callback = OnMapReadyCallback{googleMap ->
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(kullaniciKonum,12f))
        googleMap.isMyLocationEnabled = true
        binding.progressBar.visibility = View.INVISIBLE
        binding.mapLayout.visibility = View.VISIBLE


    }

}