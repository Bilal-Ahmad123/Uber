package com.example.uber.presentation.riderpresentation.bottomSheet.viewadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.example.uber.databinding.ItemVehicleBinding
import com.example.uber.domain.remote.general.model.response.NearbyVehicles
import com.example.uber.core.utils.UnsafeTrustManager
import java.io.InputStream


class CarListAdapter(private val cars:List<NearbyVehicles>) : RecyclerView.Adapter<CarListAdapter.VehicleViewHolder>() {


    inner class VehicleViewHolder(private val binding : ItemVehicleBinding) :RecyclerView.ViewHolder(binding.root){
        fun bind(car : NearbyVehicles){
            binding.tvVehicleName.text = car.name
            binding.tvMaxSeats.text= car.seats.toString()
            addImage(car)
        }

        private fun addImage(car : NearbyVehicles){
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder{
        val binding = ItemVehicleBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return VehicleViewHolder(binding)
    }

    override fun getItemCount(): Int = cars.size

    override fun onBindViewHolder(holder: CarListAdapter.VehicleViewHolder, position: Int) {
        holder.bind(cars[position])
    }
}