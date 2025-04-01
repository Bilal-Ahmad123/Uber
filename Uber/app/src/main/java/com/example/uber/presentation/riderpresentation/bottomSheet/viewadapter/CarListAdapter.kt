package com.example.uber.presentation.riderpresentation.bottomSheet.viewadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.example.uber.databinding.ItemVehicleBinding
import com.example.uber.domain.remote.general.model.response.NearbyVehicles
import com.example.uber.presentation.riderpresentation.bottomSheet.viewadapter.item.UnsafeTrustManager
import okhttp3.OkHttpClient
import java.io.InputStream
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession


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
            Glide.with(binding.root.context).load("https://192.168.18.65:5196/images/eca9101a-054b-4cbf-80ea-9cbdef77a092 - uberx.png")
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