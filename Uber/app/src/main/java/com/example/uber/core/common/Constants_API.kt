package com.example.uber.core.common

object Constants_API {
    private const val END_POINT = "192.168.18.65"
    const val BACKEND_API = "http://${END_POINT}:5231/"
    const val BACKEND_AUTH_API = "http://${END_POINT}:5232/"
    const val BACKEND_RIDER_API = "http://${END_POINT}:5213/"
    const val SOCKET_API = "ws://${END_POINT}:5213/riderhub"
}