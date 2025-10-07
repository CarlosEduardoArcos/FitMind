package com.example.fitmind.data.repository

import com.example.fitmind.model.Habito
import com.example.fitmind.data.model.Notificacion
import com.example.fitmind.data.model.Registro
import com.example.fitmind.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Central data access layer for Firebase services: Auth, Firestore and Messaging.
 * Provides suspend functions and cold Flows to be consumed by ViewModels.
 */
class FirebaseRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val messaging: FirebaseMessaging = FirebaseMessaging.getInstance(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    suspend fun registerUser(email: String, password: String, nombre: String): Result<Boolean> = withContext(ioDispatcher) {
        return@withContext try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = authResult.user?.uid ?: throw IllegalStateException("User ID is null")
            val usuario = Usuario(id = userId, nombre = nombre, email = email, fechaRegistro = System.currentTimeMillis().toString())
            db.collection("usuarios").document(userId).set(usuario).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<Boolean> = withContext(ioDispatcher) {
        return@withContext try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logoutUser() {
        auth.signOut()
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    suspend fun saveHabit(habito: Habito) = withContext(ioDispatcher) {
        val id = if (habito.id.isBlank()) db.collection("habitos").document().id else habito.id
        val toSave = habito.copy(id = id)
        db.collection("habitos").document(id).set(toSave).await()
    }

    fun getHabits(userId: String): Flow<List<Habito>> = callbackFlow {
        val subscription = db.collection("habitos")
            .whereEqualTo("usuarioId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { it.toObject<Habito>()?.copy(id = it.id) } ?: emptyList()
                trySend(items)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun addRecord(registro: Registro) = withContext(ioDispatcher) {
        val id = if (registro.id.isBlank()) db.collection("registros").document().id else registro.id
        val toSave = registro.copy(id = id)
        db.collection("registros").document(id).set(toSave).await()
    }

    fun getRecords(userId: String): Flow<List<Registro>> = callbackFlow {
        // Note: Registro model does not include usuarioId; to filter by user, join via user's habits.
        val subscription = db.collection("registros")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val items = snapshot?.documents?.mapNotNull { it.toObject<Registro>()?.copy(id = it.id) } ?: emptyList()
                trySend(items)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun sendNotification(notificacion: Notificacion) = withContext(ioDispatcher) {
        // Store notification in Firestore for history
        val id = if (notificacion.id.isBlank()) db.collection("notificaciones").document().id else notificacion.id
        val toSave = notificacion.copy(id = id)
        db.collection("notificaciones").document(id).set(toSave).await()
        // Obtain FCM token (placeholder; actual delivery requires server or callable cloud function)
        messaging.token.await()
    }
}


