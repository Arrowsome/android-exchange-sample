package com.example.exchange.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.exchange.data.*
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

    @Provides
    @Singleton
    fun provideWalletRepository(dataStore: DataStore<Preferences>): WalletRepository {
        return WalletRepositoryImpl(dataStore)
    }

}