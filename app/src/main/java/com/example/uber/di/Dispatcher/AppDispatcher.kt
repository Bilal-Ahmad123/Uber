package com.example.uber.di.Dispatcher

import com.example.uber.core.Dispatchers.IDispatchers
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers

class AppDispatcher: IDispatchers {

    override val main = Dispatchers.Main

    override val io = Dispatchers.IO

    override val db = Dispatchers.IO

    override val computation = Dispatchers.Default
}

