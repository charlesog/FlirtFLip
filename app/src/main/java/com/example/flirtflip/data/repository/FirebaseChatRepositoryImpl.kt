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
            val userIds = chatId.split("_")

            // Identificamos el ID de la otra persona (el que NO es el emisor actual)
            val otherUserId = userIds.firstOrNull { it != message.senderId } ?: ""

            // Buscamos el nombre real de esa persona en la colección "users" de tu Firebase
            var realName = ""
            if (otherUserId.isNotBlank()) {
                try {
                    val userSnapshot = firestore.collection("users")
                        .document(otherUserId)
                        .get()
                        .await()

                    // Intenta buscar "name" o "profile.name" según tu estructura en Firebase
                    realName = userSnapshot.getString("name")
                        ?: userSnapshot.getString("profile.name")
                                ?: ""
                } catch (e: Exception) {
                    // Si falla, permitimos que continúe para no congelar la app
                }
            }

            // Estructuramos los datos EXACTOS que requiere tu base de datos y modelo
            val chatData = mutableMapOf(
                "id" to chatId,
                "userIds" to userIds,
                "lastMessage" to message.text,
                "lastMessageTimestamp" to System.currentTimeMillis(),
                "lastMessageSenderId" to message.senderId, // <-- CLAVE: Guardamos quién mandó el mensaje
                "isActive" to true
            )

            // Solo sobreescribimos el nombre si logramos recuperar uno válido de la base de datos
            if (realName.isNotBlank()) {
                chatData["otherUserName"] = realName
            }

            // 1. Crear o fusionar los datos del chat raíz
            firestore.collection("chats")
                .document(chatId)
                .set(chatData, SetOptions.merge())
                .await()

            // 2. Guardar el mensaje en la subcolección interna
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

    override suspend fun deleteMessage(chatId: String, messageId: String): Result<Unit> {
        return try {
            val querySnapshot = firestore.collection("chats")
                .document(chatId)
                .collection("messages")
                .whereEqualTo("id", messageId)
                .get()
                .await()

            for (document in querySnapshot.documents) {
                firestore.collection("chats")
                    .document(chatId)
                    .collection("messages")
                    .document(document.id)
                    .delete()
                    .await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun ensureChatExists(chatId: String, userIds: List<String>): Result<Unit> {
        return try {
            val docRef = firestore.collection("chats").document(chatId)
            val snapshot = docRef.get().await()

            // Solo crear si no existe ya
            if (!snapshot.exists()) {
                val chatData = mapOf(
                    "id" to chatId,
                    "userIds" to userIds,
                    "lastMessage" to "¡Es un match! Di hola 👋",
                    "lastMessageTimestamp" to System.currentTimeMillis(),
                    "isActive" to true
                )
                docRef.set(chatData).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}