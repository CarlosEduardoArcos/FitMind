package com.example.fitmind.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun registerUser(nombre: String, email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                val rol = if (email == "carloeduardo1987@gmail.com") "admin" else "usuario"
                val userData = hashMapOf(
                    "nombre" to nombre,
                    "email" to email,
                    "rol" to rol
                )
                db.collection("users").document(uid).set(userData)
                    .addOnSuccessListener { onResult(true, null) }
                    .addOnFailureListener { onResult(false, it.message) }
            }
            .addOnFailureListener { onResult(false, it.message) }
    }

    fun loginUser(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

    fun getUserRole(uid: String, onResult: (String?) -> Unit) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val rol = document.getString("rol")
                onResult(rol)
            }
            .addOnFailureListener { onResult(null) }
    }

    fun addHabit(uid: String, habit: Map<String, Any>, onResult: (Boolean, String?) -> Unit) {
        db.collection("users").document(uid).collection("habitos")
            .add(habit)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

    fun getHabits(uid: String, onResult: (List<Map<String, Any>>) -> Unit) {
        db.collection("users").document(uid).collection("habitos")
            .addSnapshotListener { snapshot, _ ->
                val habits = snapshot?.documents?.map { it.data ?: emptyMap() } ?: emptyList()
                onResult(habits)
            }
    }

    fun deleteHabit(uid: String, habitId: String, onResult: (Boolean) -> Unit) {
        db.collection("users").document(uid)
            .collection("habitos").document(habitId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
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
        db.collection("users").document(userId).collection("habitos")
            .addSnapshotListener { snapshot, _ ->
                val habits = snapshot?.documents?.map { it.data ?: emptyMap() } ?: emptyList()
                onResult(habits)
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
        db.collection("users")
            .get()
            .addOnSuccessListener { usersSnapshot ->
                var totalHabits = 0
                var completedQueries = 0
                val totalUsers = usersSnapshot.size()
                
                if (totalUsers == 0) {
                    onResult(0)
                    return@addOnSuccessListener
                }
                
                usersSnapshot.documents.forEach { userDoc ->
                    db.collection("users").document(userDoc.id).collection("habitos")
                        .get()
                        .addOnSuccessListener { habitsSnapshot ->
                            totalHabits += habitsSnapshot.size()
                            completedQueries++
                            if (completedQueries == totalUsers) {
                                onResult(totalHabits)
                            }
                        }
                        .addOnFailureListener {
                            completedQueries++
                            if (completedQueries == totalUsers) {
                                onResult(totalHabits)
                            }
                        }
                }
            }
            .addOnFailureListener { onResult(0) }
    }

    fun getCurrentUser() = auth.currentUser
}
