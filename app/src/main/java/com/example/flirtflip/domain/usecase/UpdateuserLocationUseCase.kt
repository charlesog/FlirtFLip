package com.example.flirtflip.domain.usecase

import com.example.flirtflip.domain.repository.UserRepository
import javax.inject.Inject

class UpdateuserLocationUseCase @Inject constructor(
    private val getDeviceLocationUseCase: GetDeviceLocationUseCase,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<Unit> {
        val locationResult = getDeviceLocationUseCase()

        return locationResult.fold(
            onSuccess = { location ->
                userRepository.updateLocation(userId, location)
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}