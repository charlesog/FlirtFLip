package com.example.flirtflip.domain.usecase

import com.example.flirtflip.domain.model.AppError
import com.example.flirtflip.domain.model.Location
import com.example.flirtflip.domain.repository.LocationRepository
import javax.inject.Inject

class GetDeviceLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke(): Result<Location> {
        return try {
            val location = locationRepository.getCurrentLocation()

            if (location != null) {
                Result.success(location)
            } else {
                Result.failure(AppError.LocationDisabled)
            }
        }catch (e: Exception){
            Result.failure(AppError.UnknownError(e))
        }
    }
}