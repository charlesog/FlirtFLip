package com.example.flirtflip.domain.repository

import com.example.flirtflip.domain.model.Location

interface LocationRepository {
    suspend fun getCurrentLocation(): Location?
}