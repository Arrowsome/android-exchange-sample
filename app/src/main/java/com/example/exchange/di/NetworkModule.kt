package com.example.exchange.di

import com.example.exchange.data.ExchangeService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideExchangeService(): ExchangeService {
        return ExchangeService.create()
    }

}