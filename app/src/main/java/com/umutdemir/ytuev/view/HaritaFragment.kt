package com.umutdemir.ytuev.view

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import com.umutdemir.ytuev.R
import com.umutdemir.ytuev.Util
import com.umutdemir.ytuev.databinding.FragmentHaritaBinding
import com.umutdemir.ytuev.model.Ilan
import com.umutdemir.ytuev.viewmodel.IlanViewModel

class HaritaFragment : Fragment(), OnMapReadyCallback {

    private lateinit var viewmodel: IlanViewModel
    private lateinit var binding: FragmentHaritaBinding
    private lateinit var kullaniciKonum: LatLng
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var ilanListesi = arrayListOf<Ilan>()
    private lateinit var mapFragment : SupportMapFragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHaritaBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mapFragment = childFragmentManager.findFragmentById(R.id.mapHarita) as SupportMapFragment
        viewmodel = ViewModelProvider(requireActivity())[IlanViewModel::class.java]
        Util.konumBul(requireActivity(), callback = { konum ->
            if (konum != null) {
                kullaniciKonum = konum

                viewmodel.getIlanList(onSuccess = { ilanList: ArrayList<Ilan>? ->
                    ilanListesi = ilanList!!

                    mapFragment.getMapAsync {
                        haritaGuncelle(it,ilanListesi)
                    }


                }, onFailure = { exception ->

                })
            }
        })


    }

    override fun onMapReady(p0: GoogleMap) {
        haritaGuncelle(p0,ilanListesi)
    }

    private fun haritaGuncelle(googleMap: GoogleMap, ilanList: ArrayList<Ilan>) {
        val transformation: Transformation = object : Transformation {
            override fun transform(source: Bitmap): Bitmap {
                val scaledBitmap: Bitmap = Bitmap.createScaledBitmap(source, 200, 200, false)
                if (scaledBitmap != source) {
                    source.recycle()
                }
                return scaledBitmap
            }

            override fun key(): String {
                return "transformation"
            }


        }
        ilanList.forEach {
            val konum = LatLng(it.konumEnlem,it.konumBoylam)
            val markerOptions = MarkerOptions()
                .position(konum)
                .title(it.baslik)

            Picasso.get()
                .load(it.fotograf)
                .transform(transformation)
                .into(object : com.squareup.picasso.Target {
                    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                        googleMap.addMarker(markerOptions)
                    }

                    override fun onBitmapFailed(e: Exception, errorDrawable: Drawable?) {
                        // Hata durumunda işlem yapabilirsiniz
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                        // Yüklenirken yer tutucu gösterilebilir
                    }
                })


        }

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(kullaniciKonum, 12f))
        googleMap.isMyLocationEnabled = true
        binding.progressBar.visibility = View.INVISIBLE
        binding.mapLayout.visibility = View.VISIBLE
    }

}