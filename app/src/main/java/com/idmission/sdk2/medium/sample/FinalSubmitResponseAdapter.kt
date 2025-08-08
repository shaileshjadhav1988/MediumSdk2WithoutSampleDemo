package com.idmission.sdk2.medium.sample

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.idmission.sdk2.medium.sample.databinding.ResponseItemBinding

class FinalSubmitResponseAdapter(private val dataSet: Array<String>) :
    RecyclerView.Adapter<FinalSubmitResponseAdapter.ViewHolder>() {

    class ViewHolder(val binding: ResponseItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ResponseItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.finalSubmitResponseItem.text = dataSet[position]
    }

    override fun getItemCount(): Int = dataSet.size
}

