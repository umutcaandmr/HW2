package com.umutdemir.ytuev.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import com.umutdemir.ytuev.R
import com.umutdemir.ytuev.databinding.FragmentIlanDetayBinding
import com.umutdemir.ytuev.model.Ilan

class IlanDetayFragment(val ilan: Ilan) : Fragment() {

    private lateinit var binding: FragmentIlanDetayBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentIlanDetayBinding.inflate(inflater,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

binding.aciklama.text = ilan.aciklama
        binding.baslik.text = ilan.baslik
        binding.mesafe.text = ilan.mesafe
        binding.paylasanIsim.text = ilan.paylasanIsim
        binding.sure.text = ilan.sure
        binding.tarih.text = ilan.tarih.toDate().toString()

        Picasso.get().load(ilan.fotograf).into(binding.ilanFotograf)


    }


}