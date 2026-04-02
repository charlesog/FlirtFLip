package com.example.flirtflip.data.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth { return FirebaseAuth.getInstance() }

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore { return FirebaseFirestore.getInstance() }

    @Provides
    @Singleton
    fun provideStorage(): FirebaseStorage { return FirebaseStorage.getInstance() }

}