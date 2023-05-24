package com.umutdemir.ytuev.view

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.squareup.picasso.Picasso
import com.umutdemir.ytuev.viewmodel.ProfilViewModel
import com.umutdemir.ytuev.databinding.FragmentProfilDuzenleBinding
import com.umutdemir.ytuev.model.Kullanici
import com.umutdemir.ytuev.viewmodel.IlanViewModel
import java.util.Calendar

class ProfilDuzenleFragment : Fragment() {

    private lateinit var binding: FragmentProfilDuzenleBinding
    private lateinit var profilViewModel: ProfilViewModel
    private lateinit var ilanViewModel: IlanViewModel
    private var selectedUri: Uri = Uri.EMPTY
    private lateinit var selectedBitmap: Bitmap

    val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            selectedUri = uri
            val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
            selectedBitmap = ImageDecoder.decodeBitmap(source)
            binding.profilFotografi.setImageBitmap(selectedBitmap)

        } else {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfilDuzenleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        profilViewModel = ViewModelProvider(requireActivity())[ProfilViewModel::class.java]
        ilanViewModel = ViewModelProvider(requireActivity())[IlanViewModel::class.java]

        binding.profilFotografi.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        profilViewModel.getUserData(onSuccess = { kullanici ->

            kullanici?.let {
                Picasso.get().load(kullanici.fotograf).into(binding.profilFotografi)
                val kullaniciIsim = kullanici.isim
                binding.profilIsim.setText(kullaniciIsim)
                binding.profilMail.setText(kullanici.mail)
                binding.durumAutoComplete.setSelection(0)
                binding.profilIl.setText(kullanici.il)
                binding.profilIlce.setText(kullanici.ilce)
                binding.profilHakkinda.setText(kullanici.hakkinda)
                binding.profilBolum.setText(kullanici.bolum)
                binding.profilSure.setText(kullanici.sure)
                binding.profilNumara.setText(kullanici.numara)
                binding.profilMesafe.setText(kullanici.uzaklik)
                binding.progressBar.visibility = View.INVISIBLE
                binding.layout.visibility = View.VISIBLE

                binding.filledButton.setOnClickListener {
                    //Toast.makeText(requireContext(),binding.durumAutoComplete.text,Toast.LENGTH_SHORT).show()
                    if (selectedUri == Uri.EMPTY) {
                        val kullaniciGuncel = Kullanici(
                            fotograf = kullanici.fotograf,
                            isim = binding.profilIsim.text.toString(),
                            numara = binding.profilNumara.text.toString(),
                            mail = binding.profilMail.text.toString(),
                            il = binding.profilIl.text.toString(),
                            ilce = binding.profilIlce.text.toString(),
                            hakkinda = binding.profilHakkinda.text.toString(),
                            bolum = binding.profilBolum.text.toString(),
                            uzaklik = binding.profilMesafe.text.toString(),
                            sure = binding.profilSure.text.toString(),
                            durum = binding.durumAutoComplete.text.toString(),
                            //confirmMail = binding.profilIsim.text.toString(),
                            sinif = binding.sinifAutoComplete.text.toString()
                        )
                        kullaniciGuncelle(kullaniciGuncel)
                    }
                    else {
                        ilanViewModel.fotografPaylas(selectedUri,
                            onSuccess = { fotograf ->

                                val kullaniciGuncel = Kullanici(
                                    fotograf = fotograf,
                                    isim = binding.profilIsim.text.toString(),
                                    numara = binding.profilNumara.text.toString(),
                                    mail = binding.profilMail.text.toString(),
                                    il = binding.profilIl.text.toString(),
                                    ilce = binding.profilIlce.text.toString(),
                                    hakkinda = binding.profilHakkinda.text.toString(),
                                    bolum = binding.profilBolum.text.toString(),
                                    uzaklik = binding.profilMesafe.text.toString(),
                                    sure = binding.profilSure.text.toString(),
                                    durum = binding.durumAutoComplete.text.toString(),
                                    //confirmMail = binding.profilIsim.text.toString(),
                                    sinif = binding.sinifAutoComplete.text.toString()
                                )

                                kullaniciGuncelle(kullaniciGuncel)

                            }, onFailure = {
                            })
                    }

                }


            }

        }, onFailure = { exception ->

        })

        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select dates").setSelection(
                    Pair(
                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds()
                    )
                ).setCalendarConstraints(constraintsBuilder.build())
                .build()



        dateRangePicker.addOnPositiveButtonClickListener { selection ->
            val startDate: Long = selection.first
            val endDate: Long = selection.second

            val startCalendar = Calendar.getInstance().apply { timeInMillis = startDate }
            val endCalendar = Calendar.getInstance().apply { timeInMillis = endDate }

            val startYear = startCalendar.get(Calendar.YEAR)
            val startMonth = startCalendar.get(Calendar.MONTH)
            val endYear = endCalendar.get(Calendar.YEAR)
            val endMonth = endCalendar.get(Calendar.MONTH)

            var monthDiff = (endYear - startYear) * 12 + (endMonth - startMonth)
            if (monthDiff == 0) {
                monthDiff = 1
            }
            binding.profilSure.setText(monthDiff.toString())
        }




        binding.profilSure.setOnClickListener {
            dateRangePicker.show(parentFragmentManager, "")
        }

    }

    private fun kullaniciGuncelle(kullaniciGuncel: Kullanici) {

        profilViewModel.setUserData(kullaniciGuncel, callback = {
            if (it) {
                val activity = requireActivity() as MainActivity
                activity.loadFragment(ProfilFragment())
                Toast.makeText(
                    requireContext(),
                    "Profil Basarili Bir Sekilde Guncellendi",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    "ay noluyo noluyo",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


}