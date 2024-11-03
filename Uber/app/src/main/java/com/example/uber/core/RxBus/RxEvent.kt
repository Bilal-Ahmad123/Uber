package com.example.uber.core.RxBus

class RxEvent {
    data class EventEditTextFocus(val isPickUpEditTextFocus: Boolean, val isDropOffEditTextFocus: Boolean)
}