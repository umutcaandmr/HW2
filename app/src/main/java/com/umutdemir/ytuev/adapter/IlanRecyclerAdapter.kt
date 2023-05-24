package com.umutdemir.ytuev.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.umutdemir.ytuev.databinding.IlanRowBinding
import com.umutdemir.ytuev.model.Ilan


class IlanRecyclerAdapter(val ilanList: ArrayList<Ilan>) :
    RecyclerView.Adapter<IlanRecyclerAdapter.VH>(), Filterable {
    var filteredData = ArrayList<Ilan>()
    lateinit var context: Context

    init {
        filteredData = ilanList
    }

    class VH(val itemBinding: IlanRowBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        context = parent.context
        return VH(
            IlanRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val ilan = filteredData[position]

        holder.itemBinding.paylasanIsim.text = ilan.paylasanIsim
        if(ilan.fotograf != ""){
            Picasso.get().load(ilan.fotograf).into(holder.itemBinding.ilanFotograf)
        }
        holder.itemBinding.aciklama.text = ilan.aciklama
        holder.itemBinding.baslik.text = ilan.baslik
        holder.itemBinding.mesafe.text = ilan.mesafe
        holder.itemBinding.sure.text = ilan.sure
        holder.itemBinding.tarih.text = ilan.tarih.toDate().toString()
    }

    override fun getItemCount(): Int {
        return filteredData.size
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint.toString()

                if (query.isEmpty()) {
                    filteredData = ilanList
                } else {
                    val filteredList = ArrayList<Ilan>()
                    for (item in ilanList) {
                        if (item.paylasanIsim.lowercase().contains(query) || item.il.lowercase()
                                .contains(
                                    query
                                ) || item.ilce.lowercase().contains(query) || item.baslik.lowercase().contains(query)
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
                filteredData = results?.values as ArrayList<Ilan>
                notifyDataSetChanged()
            }
        }
    }


}