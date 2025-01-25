package com.example.uber.di.Dispatcher

import com.example.uber.core.Dispatchers.IDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class CoroutineDispatcherModule {
    @Provides
    @Singleton
    fun provideDispatchers(): IDispatchers = AppDispatcher()
}