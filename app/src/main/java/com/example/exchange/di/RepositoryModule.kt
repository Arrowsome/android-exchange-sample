package com.example.exchange.di

import com.example.exchange.data.ExchangeRepository
import com.example.exchange.data.ExchangeRepositoryImpl
import com.example.exchange.data.ExchangeService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun provideExchangeRepository(service: ExchangeService): ExchangeRepository {
        return ExchangeRepositoryImpl(service)
    }

}