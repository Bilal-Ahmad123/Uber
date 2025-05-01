package com.example.uber.core.utils

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil

//Since PolyUtil is a Java class and Java doesn't support extension methods so as a result had to create a static PolyUtilExtensions
object PolyUtilExtension {
    fun getNearestPointOnRoute(position: LatLng, routes: List<LatLng>):Pair<Int,LatLng> {
        var minDistance: Double = Double.MAX_VALUE
        var closestPoint: LatLng = position
        var currentIndex:Int =0
        for ((index,route) in routes.withIndex()) {
            val distance: Double = SphericalUtil.computeDistanceBetween(route, position)
            if (distance < minDistance) {
                minDistance = distance
                closestPoint = route
                currentIndex = index
            }
        }
        return Pair(currentIndex,closestPoint)
    }
}