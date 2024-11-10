package com.example.uber.core.interfaces

interface IActions {
    fun onBottomSheetSlide(slideOffset: Float)
    fun onBottomSheetStateChanged(newState: Int)
    fun createRouteAction()
}
