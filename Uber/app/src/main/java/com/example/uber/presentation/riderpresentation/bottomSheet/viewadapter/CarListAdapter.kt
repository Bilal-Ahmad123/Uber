package com.example.uber.presentation.riderpresentation.bottomSheet.viewadapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.uber.databinding.ItemVehicleBinding
import com.example.uber.domain.remote.general.model.response.NearbyVehicles
import com.example.uber.presentation.riderpresentation.bottomSheet.viewadapter.item.Car

class CarListAdapter(private val cars:List<Car>) : RecyclerView.Adapter<CarListAdapter.VehicleViewHolder>() {


    inner class VehicleViewHolder(private val binding : ItemVehicleBinding) :RecyclerView.ViewHolder(binding.root){
        fun bind(car : Car){
            binding.tvVehicleName.text = car.name
            binding.tvVehicleType.text = car.imageUrl
            binding.tvVehicleDistance.text = car.maxSeats.toString()
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