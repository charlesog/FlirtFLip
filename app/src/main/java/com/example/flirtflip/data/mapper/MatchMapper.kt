package com.example.flirtflip.data.mapper

import com.example.flirtflip.data.entity.MatchEntity
import com.example.flirtflip.domain.model.Match

fun MatchEntity.toDomain(): Match {
    return Match(
        id = this.id,
        idUser1 = this.idUser1,
        idUser2 = this.idUser2,
        timestamp = this.timestamp
    )
}

fun Match.toEntity(): MatchEntity {
    return MatchEntity(
        id = this.id,
        idUser1 = this.idUser1,
        idUser2 = this.idUser2,
        timestamp = this.timestamp
    )
}