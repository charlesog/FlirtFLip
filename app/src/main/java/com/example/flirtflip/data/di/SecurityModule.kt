package com.example.flirtflip.data.di

import com.example.flirtflip.data.security.BCryptSecurityProvider
import com.example.flirtflip.domain.repository.SecurityProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityModule {
    @Binds
    @Singleton
    abstract fun bindSecurityProvider(impl: BCryptSecurityProvider):
            SecurityProvider
}