package com.example.uber.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.uber.data.remote.models.mapbox.SuggestionResponse.PlaceDetail
import com.example.uber.databinding.ItemPlaceSuggestionBinding

class PlaceSuggestionAdapter(
    private val listener: (PlaceDetail) -> Unit
) : ListAdapter<PlaceDetail, PlaceSuggestionAdapter.PlaceViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = ItemPlaceSuggestionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlaceViewHolder(private val binding: ItemPlaceSuggestionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(place: PlaceDetail) {
            binding.placeName.text = place.name
            binding.placeAddress.text = place.fullAddress
            binding.root.setOnClickListener { listener(place) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PlaceDetail>() {
        override fun areItemsTheSame(oldItem: PlaceDetail, newItem: PlaceDetail): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: PlaceDetail, newItem: PlaceDetail): Boolean = oldItem == newItem
    }
}