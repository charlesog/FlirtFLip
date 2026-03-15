package com.example.flirtflip.domain.repository

interface SecurityProvider {
    fun hashPassword(password: String): String
    fun verifyPassword(password: String, hashPassword: String): Boolean
}