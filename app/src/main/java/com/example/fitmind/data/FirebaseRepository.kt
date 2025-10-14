package com.example.fitmind.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun registerUser(nombre: String, email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        Log.d("Firestore", "Iniciando registro de usuario: $email")
        
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                val rol = if (email == "carloeduardo1987@gmail.com") "admin" else "usuario"
                
                Log.d("Firestore", "Usuario creado en Auth con UID: $uid")
                
                // Crear documento del usuario en Firestore con estructura correcta
                val userData = hashMapOf(
                    "nombre" to nombre,
                    "email" to email,
                    "rol" to rol,
                    "habitos" to emptyList<Map<String, Any>>(), // Lista vacía inicial
                    "fechaRegistro" to System.currentTimeMillis()
                )
                
                Log.d("Firestore", "Guardando datos del usuario en Firestore: $userData")
                
                // Usar merge para no sobrescribir datos existentes
                db.collection("users").document(uid).set(userData, com.google.firebase.firestore.SetOptions.merge())
                    .addOnSuccessListener { 
                        Log.d("Firestore", "Usuario guardado exitosamente en Firestore")
                        onResult(true, null) 
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Firestore", "Error al guardar usuario en Firestore", exception)
                        onResult(false, exception.message) 
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error al crear usuario en Auth", exception)
                onResult(false, exception.message) 
            }
    }

    fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        Log.d("Firestore", "Iniciando login para usuario: $email")
        
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { 
                Log.d("Firestore", "Login exitoso para usuario: $email")
                onResult(true, null) 
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error en login para usuario: $email", exception)
                onResult(false, exception.message) 
            }
    }

    fun getUserRole(uid: String, onResult: (String?) -> Unit) {
        Log.d("Firestore", "Obteniendo rol para usuario UID: $uid")
        
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val rol = document.getString("rol")
                    Log.d("Firestore", "Rol obtenido: $rol para UID: $uid")
                    onResult(rol)
                } else {
                    Log.w("Firestore", "Documento de usuario no encontrado para UID: $uid")
                    onResult(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error al obtener rol para UID: $uid", exception)
                onResult(null) 
            }
    }

    fun updateUserRole(uid: String, role: String) {
        Log.d("Firestore", "Actualizando rol a '$role' para usuario UID: $uid")
        
        db.collection("users").document(uid).update("rol", role)
            .addOnSuccessListener {
                Log.d("Firestore", "Rol actualizado exitosamente a '$role' para UID: $uid")
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error al actualizar rol para UID: $uid", exception)
            }
    }

    fun addHabit(uid: String, habit: Map<String, Any>, onResult: (Boolean, String?) -> Unit) {
        Log.d("Firestore", "Agregando hábito para usuario UID: $uid")
        
        // Primero asegurar que el campo hábitos existe
        ensureHabitsFieldExists(uid) {
            // Usar FieldValue.arrayUnion para agregar a la lista de hábitos
            db.collection("users").document(uid).update(
                "habitos", FieldValue.arrayUnion(habit)
            )
                .addOnSuccessListener { 
                    Log.d("Firestore", "Hábito agregado exitosamente para UID: $uid")
                    onResult(true, null) 
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error al agregar hábito para UID: $uid", exception)
                    onResult(false, exception.message) 
                }
        }
    }

    fun getHabits(uid: String, onResult: (List<Map<String, Any>>) -> Unit) {
        Log.d("Firestore", "Obteniendo hábitos para usuario UID: $uid")
        
        db.collection("users").document(uid).addSnapshotListener { document, exception ->
            if (exception != null) {
                Log.e("Firestore", "Error al obtener hábitos para UID: $uid", exception)
                onResult(emptyList())
                return@addSnapshotListener
            }
            
            if (document != null && document.exists()) {
                @Suppress("UNCHECKED_CAST")
                val habits = document.get("habitos") as? List<Map<String, Any>> ?: emptyList()
                Log.d("Firestore", "Hábitos obtenidos: ${habits.size} para UID: $uid")
                onResult(habits)
            } else {
                Log.w("Firestore", "Documento de usuario no encontrado para UID: $uid")
                onResult(emptyList())
            }
        }
    }
    
    /**
     * Obtiene hábitos en tiempo real con listener activo para estadísticas
     */
    fun getHabitsRealtime(uid: String, onResult: (List<Map<String, Any>>) -> Unit) {
        Log.d("Firestore", "Iniciando listener en tiempo real para hábitos del usuario UID: $uid")
        
        db.collection("users").document(uid).addSnapshotListener { document, exception ->
            if (exception != null) {
                Log.e("Firestore", "Error en listener tiempo real para UID: $uid", exception)
                onResult(emptyList())
                return@addSnapshotListener
            }
            
            if (document != null && document.exists()) {
                @Suppress("UNCHECKED_CAST")
                val habits = document.get("habitos") as? List<Map<String, Any>> ?: emptyList()
                Log.d("Firestore", "📊 Actualización tiempo real: ${habits.size} hábitos para UID: $uid")
                onResult(habits)
            } else {
                Log.w("Firestore", "Documento no encontrado en tiempo real para UID: $uid")
                onResult(emptyList())
            }
        }
    }

    fun deleteHabit(uid: String, habit: Map<String, Any>, onResult: (Boolean) -> Unit) {
        Log.d("Firestore", "Eliminando hábito para usuario UID: $uid")
        
        // Usar FieldValue.arrayRemove para eliminar de la lista
        db.collection("users").document(uid).update(
            "habitos", FieldValue.arrayRemove(habit)
        )
            .addOnSuccessListener { 
                Log.d("Firestore", "Hábito eliminado exitosamente para UID: $uid")
                onResult(true) 
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error al eliminar hábito para UID: $uid", exception)
                onResult(false) 
            }
    }
    
    /**
     * Elimina un hábito específico por ID desde Firebase
     */
    suspend fun deleteHabitFromFirebase(userId: String, habitId: String): Boolean {
        return try {
            Log.d("Firestore", "Eliminando hábito $habitId para usuario $userId")
            
            // Obtener todos los hábitos del usuario
            val userDoc = db.collection("users").document(userId).get().await()
            val habits = userDoc.get("habitos") as? List<Map<String, Any>> ?: emptyList()
            
            // Encontrar y eliminar el hábito específico
            val updatedHabits = habits.filter { it["id"] != habitId }
            
            // Actualizar la lista en Firebase
            db.collection("users").document(userId).update("habitos", updatedHabits).await()
            
            Log.d("Firestore", "Hábito $habitId eliminado exitosamente para usuario $userId")
            true
        } catch (e: Exception) {
            Log.e("Firestore", "Error al eliminar hábito $habitId para usuario $userId", e)
            false
        }
    }

    fun getAllUsers(onResult: (List<Pair<String, Map<String, Any>>>) -> Unit) {
        db.collection("users")
            .addSnapshotListener { snapshot, _ ->
                val users = snapshot?.documents?.map {
                    it.id to (it.data ?: emptyMap())
                } ?: emptyList()
                onResult(users)
            }
    }

    fun getUserHabits(userId: String, onResult: (List<Map<String, Any>>) -> Unit) {
        Log.d("Firestore", "Obteniendo hábitos del usuario para UID: $userId")
        
        db.collection("users").document(userId).addSnapshotListener { document, exception ->
            if (exception != null) {
                Log.e("Firestore", "Error al obtener hábitos del usuario para UID: $userId", exception)
                onResult(emptyList())
                return@addSnapshotListener
            }
            
            if (document != null && document.exists()) {
                @Suppress("UNCHECKED_CAST")
                val habits = document.get("habitos") as? List<Map<String, Any>> ?: emptyList()
                Log.d("Firestore", "Hábitos del usuario obtenidos: ${habits.size} para UID: $userId")
                onResult(habits)
            } else {
                Log.w("Firestore", "Documento de usuario no encontrado para UID: $userId")
                onResult(emptyList())
            }
        }
    }

    fun getUsersCount(onResult: (Int) -> Unit) {
        db.collection("users")
            .get()
            .addOnSuccessListener { snapshot ->
                onResult(snapshot.size())
            }
            .addOnFailureListener { onResult(0) }
    }

    fun getTotalHabitsCount(onResult: (Int) -> Unit) {
        Log.d("Firestore", "Obteniendo conteo total de hábitos")
        
        db.collection("users")
            .get()
            .addOnSuccessListener { usersSnapshot ->
                var totalHabits = 0
                var completedQueries = 0
                val totalUsers = usersSnapshot.size()
                
                Log.d("Firestore", "Procesando $totalUsers usuarios para conteo de hábitos")
                
                if (totalUsers == 0) {
                    onResult(0)
                    return@addOnSuccessListener
                }
                
                usersSnapshot.documents.forEach { userDoc ->
                    val userData = userDoc.data
                    @Suppress("UNCHECKED_CAST")
                    val userHabits = userData?.get("habitos") as? List<*> ?: emptyList<Any>()
                    totalHabits += userHabits.size
                    completedQueries++
                    
                    if (completedQueries == totalUsers) {
                        Log.d("Firestore", "Conteo total de hábitos: $totalHabits")
                        onResult(totalHabits)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error al obtener conteo total de hábitos", exception)
                onResult(0) 
            }
    }

    fun getCurrentUser() = auth.currentUser
    
    /**
     * Inicializa el campo hábitos en el documento del usuario si no existe
     */
    fun initializeUserHabitsField(uid: String, onResult: (Boolean, String?) -> Unit) {
        Log.d("Firestore", "Inicializando campo hábitos para usuario UID: $uid")
        
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val habitos = document.get("habitos")
                    if (habitos == null) {
                        Log.d("Firestore", "Campo hábitos no existe, creándolo para UID: $uid")
                        db.collection("users").document(uid).update("habitos", emptyList<Map<String, Any>>())
                            .addOnSuccessListener {
                                Log.d("Firestore", "Campo hábitos inicializado exitosamente para UID: $uid")
                                onResult(true, null)
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Firestore", "Error al inicializar campo hábitos para UID: $uid", exception)
                                onResult(false, exception.message)
                            }
                    } else {
                        Log.d("Firestore", "Campo hábitos ya existe para UID: $uid")
                        onResult(true, null)
                    }
                } else {
                    Log.w("Firestore", "Documento de usuario no encontrado para UID: $uid")
                    onResult(false, "Usuario no encontrado")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error al verificar campo hábitos para UID: $uid", exception)
                onResult(false, exception.message)
            }
    }
    
    /**
     * Obtiene información completa del usuario
     */
    fun getUserInfo(uid: String, onResult: (Map<String, Any>?) -> Unit) {
        Log.d("Firestore", "Obteniendo información del usuario UID: $uid")
        
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userData = document.data ?: emptyMap()
                    Log.d("Firestore", "Información del usuario obtenida para UID: $uid")
                    onResult(userData)
                } else {
                    Log.w("Firestore", "Documento de usuario no encontrado para UID: $uid")
                    onResult(null)
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error al obtener información del usuario para UID: $uid", exception)
                onResult(null)
            }
    }
    
    /**
     * Inicializa y valida los datos del usuario al iniciar sesión
     * Verifica que exista el campo hábitos y lo crea si no existe
     */
    fun initializeUserData(uid: String, onResult: (Boolean, String?) -> Unit) {
        Log.d("Firestore", "Inicializando datos del usuario UID: $uid")
        
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val userData = document.data ?: emptyMap() ?: emptyMap()
                    
                    // Verificar si el campo hábitos existe
                    val habitos = userData["habitos"]
                    
                    if (habitos == null) {
                        Log.d("Firestore", "Campo 'habitos' no encontrado, agregándolo automáticamente para UID: $uid")
                        
                        // Agregar el campo hábitos usando merge para no sobrescribir otros campos
                        val updates = hashMapOf<String, Any>(
                            "habitos" to emptyList<Map<String, Any>>()
                        )
                        
                        db.collection("users").document(uid).update(updates)
                            .addOnSuccessListener {
                                Log.d("Firestore", "Campo 'habitos' agregado automáticamente para UID: $uid")
                                onResult(true, "Campo hábitos inicializado")
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Firestore", "Error al agregar campo 'habitos' para UID: $uid", exception)
                                // Intentar crear el documento completo si falla la actualización
                                createCompleteUserDocument(uid, userData, onResult)
                            }
                    } else {
                        Log.d("Firestore", "Campo 'habitos' ya existe para UID: $uid")
                        
                        // Verificar si hay otros campos obligatorios faltantes
                        val requiredFields = listOf("nombre", "email", "rol")
                        val missingFields = requiredFields.filter { userData[it] == null }
                        
                        if (missingFields.isNotEmpty()) {
                            Log.w("Firestore", "Campos faltantes para UID: $uid - $missingFields")
                            onResult(false, "Campos obligatorios faltantes: ${missingFields.joinToString(", ")}")
                        } else {
                            onResult(true, "Datos del usuario válidos")
                        }
                    }
                } else {
                    Log.w("Firestore", "Documento de usuario no encontrado para UID: $uid")
                    onResult(false, "Usuario no encontrado en Firestore")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error al verificar datos del usuario para UID: $uid", exception)
                onResult(false, exception.message)
            }
    }
    
    /**
     * Crea un documento completo del usuario si falla la actualización
     */
    private fun createCompleteUserDocument(uid: String, existingData: Map<String, Any>, onResult: (Boolean, String?) -> Unit) {
        Log.d("Firestore", "Creando documento completo del usuario para UID: $uid")
        
        val userData = hashMapOf<String, Any>(
            "habitos" to emptyList<Map<String, Any>>(),
            "fechaRegistro" to System.currentTimeMillis()
        )
        
        // Agregar campos existentes que no sean null
        existingData.forEach { (key, value) ->
            if (value != null) {
                userData[key] = value
            }
        }
        
        // Usar merge para no sobrescribir datos existentes
        db.collection("users").document(uid).set(userData, com.google.firebase.firestore.SetOptions.merge())
            .addOnSuccessListener {
                Log.d("Firestore", "Documento completo del usuario creado para UID: $uid")
                onResult(true, "Documento de usuario creado/actualizado")
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error al crear documento completo para UID: $uid", exception)
                onResult(false, exception.message)
            }
    }
    
    /**
     * Valida que el campo hábitos existe antes de agregar un hábito
     */
    fun ensureHabitsFieldExists(uid: String, onComplete: () -> Unit) {
        Log.d("Firestore", "Verificando campo hábitos para UID: $uid")
        
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val habitos = document.get("habitos")
                    if (habitos == null) {
                        Log.d("Firestore", "Campo hábitos no existe, creándolo para UID: $uid")
                        db.collection("users").document(uid).update("habitos", emptyList<Map<String, Any>>())
                            .addOnSuccessListener {
                                Log.d("Firestore", "Campo hábitos creado exitosamente para UID: $uid")
                                onComplete()
                            }
                            .addOnFailureListener { exception ->
                                Log.e("Firestore", "Error al crear campo hábitos para UID: $uid", exception)
                                onComplete() // Continuar de todas formas
                            }
                    } else {
                        Log.d("Firestore", "Campo hábitos ya existe para UID: $uid")
                        onComplete()
                    }
                } else {
                    Log.w("Firestore", "Documento de usuario no encontrado para UID: $uid")
                    onComplete() // Continuar de todas formas
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error al verificar campo hábitos para UID: $uid", exception)
                onComplete() // Continuar de todas formas
            }
    }
    
    /**
     * Migra todos los usuarios existentes para asegurar que tengan el campo hábitos
     * Esta función debe ejecutarse una sola vez para migrar usuarios antiguos
     */
    fun migrateAllUsersToHaveHabitsField(onProgress: (Int, Int) -> Unit, onComplete: (Boolean, String?) -> Unit) {
        Log.d("Firestore", "Iniciando migración de usuarios para agregar campo hábitos")
        
        db.collection("users").get()
            .addOnSuccessListener { snapshot ->
                val totalUsers = snapshot.size()
                var processedUsers = 0
                var successCount = 0
                var errorCount = 0
                
                Log.d("Firestore", "Procesando $totalUsers usuarios para migración")
                
                if (totalUsers == 0) {
                    onComplete(true, "No hay usuarios para migrar")
                    return@addOnSuccessListener
                }
                
                snapshot.documents.forEach { document ->
                    val uid = document.id
                    val userData = document.data ?: emptyMap()
                    
                    // Verificar si el campo hábitos existe
                    val habitos = userData["habitos"]
                    
                    if (habitos == null) {
                        Log.d("Firestore", "Migrando usuario UID: $uid - agregando campo hábitos")
                        
                        db.collection("users").document(uid).update(
                            "habitos", emptyList<Map<String, Any>>()
                        )
                            .addOnSuccessListener {
                                successCount++
                                processedUsers++
                                onProgress(processedUsers, totalUsers)
                                Log.d("Firestore", "Usuario UID: $uid migrado exitosamente")
                                
                                if (processedUsers == totalUsers) {
                                    Log.d("Firestore", "Migración completada: $successCount exitosos, $errorCount errores")
                                    onComplete(true, "Migración completada: $successCount usuarios actualizados")
                                }
                            }
                            .addOnFailureListener { exception ->
                                errorCount++
                                processedUsers++
                                onProgress(processedUsers, totalUsers)
                                Log.e("Firestore", "Error al migrar usuario UID: $uid", exception)
                                
                                if (processedUsers == totalUsers) {
                                    Log.d("Firestore", "Migración completada con errores: $successCount exitosos, $errorCount errores")
                                    onComplete(false, "Migración completada con $errorCount errores")
                                }
                            }
                    } else {
                        Log.d("Firestore", "Usuario UID: $uid ya tiene campo hábitos")
                        processedUsers++
                        onProgress(processedUsers, totalUsers)
                        
                        if (processedUsers == totalUsers) {
                            Log.d("Firestore", "Migración completada: todos los usuarios ya tenían el campo hábitos")
                            onComplete(true, "Todos los usuarios ya tenían el campo hábitos")
                        }
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error al obtener usuarios para migración", exception)
                onComplete(false, exception.message)
            }
    }
    
    /**
     * Obtiene todos los hábitos de un usuario desde Firebase
     */
    suspend fun getHabitsFromFirebase(userId: String): List<Map<String, Any>> {
        return try {
            Log.d("Firestore", "Obteniendo hábitos desde Firebase para usuario $userId")
            
            val userDoc = db.collection("users").document(userId).get().await()
            val habits = userDoc.get("habitos") as? List<Map<String, Any>> ?: emptyList()
            
            Log.d("Firestore", "Hábitos obtenidos desde Firebase: ${habits.size} para usuario $userId")
            habits
        } catch (e: Exception) {
            Log.e("Firestore", "Error al obtener hábitos desde Firebase para usuario $userId", e)
            emptyList()
        }
    }
    
    /**
     * Verifica la estructura de Firestore y reporta problemas
     */
    fun validateFirestoreStructure(onResult: (Boolean, String?) -> Unit) {
        Log.d("Firestore", "Validando estructura de Firestore")
        
        db.collection("users").limit(10).get()
            .addOnSuccessListener { snapshot ->
                val issues = mutableListOf<String>()
                var validUsers = 0
                
                snapshot.documents.forEach { document ->
                    val uid = document.id
                    val data = document.data ?: emptyMap()
                    
                    // Verificar campos obligatorios
                    val requiredFields = listOf("nombre", "email", "rol")
                    val missingFields = requiredFields.filter { data[it] == null }
                    
                    if (missingFields.isNotEmpty()) {
                        issues.add("Usuario $uid: faltan campos ${missingFields.joinToString(", ")}")
                    }
                    
                    // Verificar campo hábitos
                    if (data["habitos"] == null) {
                        issues.add("Usuario $uid: falta campo hábitos")
                    } else {
                        validUsers++
                    }
                }
                
                if (issues.isEmpty()) {
                    Log.d("Firestore", "Estructura de Firestore válida: $validUsers usuarios verificados")
                    onResult(true, "Estructura válida: $validUsers usuarios verificados")
                } else {
                    Log.w("Firestore", "Problemas encontrados en estructura: ${issues.size}")
                    onResult(false, "Problemas encontrados: ${issues.joinToString("; ")}")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error al validar estructura de Firestore", exception)
                onResult(false, exception.message)
            }
    }
}
