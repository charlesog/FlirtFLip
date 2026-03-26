package com.example.flirtflip.data.di

import com.example.flirtflip.data.repository.FirebaseAuthRepositoryImpl
import com.example.flirtflip.data.repository.FirebaseUserRepositoryImpl
import com.example.flirtflip.domain.repository.AuthRepository
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
        impl: FirebaseAuthRepositoryImpl
    ): AuthRepository
    
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: FirebaseUserRepositoryImpl
    ): UserRepository
}

