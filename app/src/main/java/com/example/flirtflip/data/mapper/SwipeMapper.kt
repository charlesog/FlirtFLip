package com.example.flirtflip.data.mapper

import com.example.flirtflip.data.entity.SwipeEntity
import com.example.flirtflip.domain.model.Swipe
import com.example.flirtflip.domain.model.SwipeType

fun SwipeEntity.toDomain(): Swipe {
    return Swipe(
        id = this.id,
        fromUserId = this.fromUserId,
        toUserId = this.toUserId,
        type = try {
            SwipeType.valueOf(this.type)
        } catch (e: Exception) {
            SwipeType.DISLIKE
        },
        timestamp = this.timestamp
    )
}

fun Swipe.toEntity(): SwipeEntity {
    return SwipeEntity(
        id = this.id,
        fromUserId = this.fromUserId,
        toUserId = this.toUserId,
        type = this.type.name,
        timestamp = this.timestamp
    )
}