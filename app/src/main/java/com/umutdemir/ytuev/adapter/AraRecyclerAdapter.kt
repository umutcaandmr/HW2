package com.umutdemir.ytuev.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.umutdemir.ytuev.R
import com.umutdemir.ytuev.databinding.OgrenciAraRowBinding
import com.umutdemir.ytuev.model.Kullanici

class AraRecyclerAdapter(val kullaniciList: ArrayList<Kullanici>) :
    RecyclerView.Adapter<AraRecyclerAdapter.VH>(), Filterable {
    var filteredData = ArrayList<Kullanici>()
    lateinit var context: Context

    init {
        filteredData = kullaniciList
    }

    class VH(val itemBinding: OgrenciAraRowBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        context = parent.context
        return VH(
            OgrenciAraRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val kullanici = filteredData[position]
        Picasso.get().load(kullanici.fotograf).into(holder.itemBinding.profilFotografi)
        holder.itemBinding.profilIsim.text = kullanici.isim
        holder.itemBinding.profilKonum.text = kullanici.ilce + " / " + kullanici.il



    }

    override fun getItemCount(): Int {
        return filteredData.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint.toString()

                if (query.isEmpty()) {
                    filteredData = kullaniciList
                } else {
                    val filteredList = ArrayList<Kullanici>()
                    for (item in kullaniciList) {
                        if (item.isim.lowercase().contains(query) || item.mail.lowercase().contains(
                                query
                            ) || item.bolum.lowercase().contains(query) || item.bolum.lowercase().contains(query)
                            || item.numara.lowercase().contains(query)
                        ) {
                            filteredList.add(item)
                        }
                    }
                    filteredData = filteredList
                }

                val results = FilterResults()
                results.values = filteredData
                return results
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredData = results?.values as ArrayList<Kullanici>
                notifyDataSetChanged()
            }
        }
    }


}