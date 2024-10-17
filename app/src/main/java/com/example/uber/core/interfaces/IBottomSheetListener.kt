package com.example.uber.core.interfaces

import com.example.uber.core.Dispatchers.IDispatchers

interface IBottomSheetListener {
    fun onBottomSheetSlide(slideOffset: Float)
    fun onBottomSheetStateChanged(newState: Int)
}
