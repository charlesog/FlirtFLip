package com.example.flirtflip.data.mapper

import com.example.flirtflip.data.entity.ChatEntity
import com.example.flirtflip.domain.model.Chat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

fun ChatEntity.toDomain(): Chat {
    return Chat(
        id = this.id,
        userIds = this.userIds,
        lastMessage = this.lastMessage,
        lastMessageTimestamp = this.lastMessageTimestamp?.let {
            LocalDateTime.ofInstant(
                Instant.ofEpochMilli(it),
                ZoneId.systemDefault()
            )
        },
        isActive = this.isActive,
        otherUserName = "",
        otherUserProfileUrl = ""
    )
}

fun Chat.toEntity(): ChatEntity {
    return ChatEntity(
        id = this.id,
        userIds = this.userIds,
        lastMessage = this.lastMessage,
        lastMessageTimestamp = this.lastMessageTimestamp?.atZone(ZoneId.systemDefault())
            ?.toInstant()
            ?.toEpochMilli(),
        isActive = this.isActive
    )
}