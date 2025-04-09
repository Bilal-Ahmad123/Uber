package com.example.uber.presentation.riderpresentation.bottomSheet.viewadapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.example.uber.core.utils.Helper
import com.example.uber.core.utils.UnsafeTrustManager
import com.example.uber.databinding.ItemVehicleBinding
import com.example.uber.domain.remote.general.model.response.NearbyVehicles
import java.io.InputStream


class CarListAdapter(
    private val cars: List<NearbyVehicles>,
    private val onItemClick: (NearbyVehicles) -> Unit
) : RecyclerView.Adapter<CarListAdapter.VehicleViewHolder>() {

    private var selectedPosition = -1

    inner class VehicleViewHolder(
        private val binding: ItemVehicleBinding,
        private val onItemClicked: (Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.llVehicle.setOnClickListener {
                onItemClicked(adapterPosition)
            }
        }

        fun bind(car: NearbyVehicles) {
            binding.tvVehicleName.text = car.name
            binding.tvMaxSeats.text = car.seats.toString()
            binding.tvArrivalTime.text = Helper.calculateTimeWithVehicleTime(car.time)
            binding.tvEta.text = Helper.showTimeAway(car.time)
            binding.tvFare.text = Helper.showCurrency(car.fare)
            addImage(car)
        }

        private fun addImage(car: NearbyVehicles) {
            val unsafeClient = UnsafeTrustManager.getUnsafeOkHttpClient()

            val glideModule = OkHttpUrlLoader.Factory(unsafeClient)
            Glide.get(binding.root.context).registry.replace(
                GlideUrl::class.java,
                InputStream::class.java,
                glideModule
            )
            Glide.with(binding.root.context).load(car.image)
                .skipMemoryCache(true)
                .into(binding.ivVehicle)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val binding = ItemVehicleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VehicleViewHolder(binding) {
            onItemClick(cars[it])
        }
    }

    override fun getItemCount(): Int = cars.size

    override fun onBindViewHolder(holder: CarListAdapter.VehicleViewHolder, position: Int) {
        holder.bind(cars[position])
        holder.itemView.isSelected = position == selectedPosition
        holder.itemView.setOnClickListener {
            selectedPosition = if (selectedPosition == position) {
                -1
            } else {
                position
            }

            //MUD code needs to be refactored
            runCatching {
                notifyDataSetChanged()
            }.onFailure {
                Log.e("Error",it.toString())
            }
        }
    }
}