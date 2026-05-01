package com.example.flirtflip.data.mapper

import com.example.flirtflip.data.entity.MessageEntity
import com.example.flirtflip.domain.model.Message
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun MessageEntity.toDomain(): Message {
    return Message(
        id = this.id,
        chatId = this.chatId,
        senderId = this.senderId,
        text = this.text,
        timestamp = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(this.timestamp),
            ZoneId.systemDefault()
        ),
        liked = this.liked,
        isRead = this.isRead
    )
}

fun Message.toEntity(): MessageEntity {
    return MessageEntity(
        id = this.id,
        chatId = this.chatId,
        senderId = this.senderId,
        text = this.text,
        timestamp = this.timestamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
        liked = this.liked,
        isRead = this.isRead
    )
}