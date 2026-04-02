package com.example.flirtflip.data.di

import com.example.flirtflip.data.repository.FirebaseAuthRepositoryImpl
import com.example.flirtflip.data.repository.FirebaseMatchRepositoryImpl
import com.example.flirtflip.data.repository.FirebaseUserRepositoryImpl
import com.example.flirtflip.data.repository.FusedLocationRepositoryImpl
import com.example.flirtflip.domain.repository.AuthRepository
import com.example.flirtflip.domain.repository.LocationRepository
import com.example.flirtflip.domain.repository.MatchRepository
import com.example.flirtflip.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: FirebaseAuthRepositoryImpl
    ): AuthRepository
    
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: FirebaseUserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindMatchRepository(
        matchRepositoryImpl: FirebaseMatchRepositoryImpl
    ): MatchRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        locationRepositoryImpl: FusedLocationRepositoryImpl
    ): LocationRepository
}

