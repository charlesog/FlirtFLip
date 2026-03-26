package com.example.flirtflip.data.security

import com.example.flirtflip.domain.repository.SecurityProvider
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BCryptSecurityProvider @Inject constructor() : SecurityProvider{

    private companion object {
        const val PEPPER = "FUW5PE_FlitrtFlip_T4P9D/RWG9R$%12"
        const val BYCRYPT_LONG_ROUNDS = 12
    }

    override fun hashPassword(password: String): String {
        val passwordWithPepper = password + PEPPER
        return BCrypt.hashpw(passwordWithPepper, BCrypt.gensalt(BYCRYPT_LONG_ROUNDS))
    }

    override fun verifyPassword(password: String, hashPassword: String): Boolean {
        return try {
            val passwordWithPepper = password + PEPPER
            BCrypt.checkpw(passwordWithPepper, hashPassword)
        } catch (_: Exception) {
            false
        }
    }
}