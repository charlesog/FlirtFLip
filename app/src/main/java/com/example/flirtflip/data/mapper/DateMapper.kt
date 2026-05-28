package com.example.flirtflip.data.mapper

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

fun LocalDateTime.toEpochMillis(): Long {
    return this.toInstant(ZoneOffset.UTC).toEpochMilli()
}

fun Long.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneOffset.UTC)
}