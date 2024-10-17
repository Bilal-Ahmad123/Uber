package com.example.uber.core.Dispatchers

import kotlinx.coroutines.CoroutineDispatcher

interface IDispatchers {
    val main: CoroutineDispatcher

    val io: CoroutineDispatcher

    val db: CoroutineDispatcher

    val computation: CoroutineDispatcher
}