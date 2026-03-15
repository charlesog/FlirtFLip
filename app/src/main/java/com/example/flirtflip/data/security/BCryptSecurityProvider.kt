package com.example.flirtflip.data.security

import com.example.flirtflip.domain.repository.SecurityProvider
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BCryptSecurityProvider @Inject constructor() : SecurityProvider{
    override fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    override fun verifyPassword(password: String, hashPassword: String): Boolean {
        return try {
            BCrypt.checkpw(password, hashPassword)
        } catch (_: Exception) {
            false
        }
    }
}