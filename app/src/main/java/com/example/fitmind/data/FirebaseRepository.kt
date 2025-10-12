package com.example.fitmind.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

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
                
                db.collection("users").document(uid).set(userData)
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

    fun addHabit(uid: String, habit: Map<String, Any>, onResult: (Boolean, String?) -> Unit) {
        Log.d("Firestore", "Agregando hábito para usuario UID: $uid")
        
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
                // Si el campo hábitos no existe, crearlo primero
                if (exception.message?.contains("No document to update") == true) {
                    Log.d("Firestore", "Creando campo hábitos inicial para UID: $uid")
                    db.collection("users").document(uid).update(
                        "habitos", listOf(habit)
                    )
                        .addOnSuccessListener { 
                            Log.d("Firestore", "Campo hábitos creado y hábito agregado para UID: $uid")
                            onResult(true, null) 
                        }
                        .addOnFailureListener { createException ->
                            Log.e("Firestore", "Error al crear campo hábitos para UID: $uid", createException)
                            onResult(false, createException.message) 
                        }
                } else {
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
                    val userData = document.data
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
}
