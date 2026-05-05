package com.example.flirtflip.data.repository

import com.example.flirtflip.data.entity.ChatEntity
import com.example.flirtflip.data.entity.MessageEntity
import com.example.flirtflip.data.mapper.toDomain
import com.example.flirtflip.data.mapper.toEntity
import com.example.flirtflip.domain.model.Chat
import com.example.flirtflip.domain.model.Message
import com.example.flirtflip.domain.repository.ChatRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ChatRepository {

    override fun getMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val subscription = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(MessageEntity::class.java)?.toDomain()
                } ?: emptyList()

                trySend(messages)
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun sendMessage(chatId: String, message: Message): Result<Unit> {
        return try {
            val messageEntity = message.toEntity()

            // Extraemos los IDs del chatId (asumiendo formato "ID1_ID2")
            val userIds = chatId.split("_")

            // Preparamos los datos del chat para que sea visible en la lista
            val chatData = mapOf(
                "id" to chatId,
                "userIds" to userIds,
                "lastMessage" to message.text, // USANDO .text SEGÚN TU MODELO
                "timestamp" to message.timestamp.toString() // Firebase prefiere String o Date
            )

            // 1. Asegurar que el documento del chat existe
            firestore.collection("chats")
                .document(chatId)
                .set(chatData, SetOptions.merge())
                .await()

            // 2. Guardar el mensaje en la subcolección
            firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .add(messageEntity)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getMyChats(userId: String): Flow<List<Chat>> = callbackFlow {
        val subscription = firestore.collection("chats")
            .whereArrayContains("userIds", userId)
            .addSnapshotListener { snapshot, _ ->
                val chats = snapshot?.documents?.mapNotNull {
                    it.toObject(ChatEntity::class.java)?.toDomain()
                } ?: emptyList()
                trySend(chats)
            }
        awaitClose { subscription.remove() }
    }
}