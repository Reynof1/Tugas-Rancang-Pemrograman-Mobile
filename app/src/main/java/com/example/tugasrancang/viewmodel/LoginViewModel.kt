package com.example.tugasrancang.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val auth = Firebase.auth
    // Ganti referensi ke Realtime Database
    private val database = Firebase.database.reference

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _navigationState = MutableStateFlow<NavigationRoutes?>(null)
    val navigationState = _navigationState.asStateFlow()

    fun handleLogin(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _errorMessage.value = "Email dan password tidak boleh kosong."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            auth.signInWithEmailAndPassword(email.trim(), pass)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        checkUserRoleAndNavigate(task.result.user!!.uid)
                    } else {
                        _errorMessage.value = task.exception?.message ?: "Login gagal."
                        _isLoading.value = false
                    }
                }
        }
    }

    private fun checkUserRoleAndNavigate(uid: String) {
        // Mengambil data dari node "users" dengan child UID
        database.child("users").child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _isLoading.value = false
                if (snapshot.exists()) {
                    // Ambil nilai 'role' dari snapshot
                    val role = snapshot.child("role").getValue(String::class.java)
                    _navigationState.value = when (role) {
                        "mahasiswa" -> NavigationRoutes.MahasiswaHome
                        "dosen" -> NavigationRoutes.DosenHome
                        "kaprodi" -> NavigationRoutes.KaprodiHome
                        else -> null
                    }
                } else {
                    _errorMessage.value = "Data user tidak ditemukan."
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _isLoading.value = false
                _errorMessage.value = "Gagal mengambil data user: ${error.message}"
            }
        })
    }

    fun onNavigationComplete() {
        _navigationState.value = null
    }

    fun onErrorMessageShown() {
        _errorMessage.value = null
    }
}

// Sealed class ini tidak berubah
sealed class NavigationRoutes(val route: String) {
    data object MahasiswaHome : NavigationRoutes("mahasiswa_home")
    data object DosenHome : NavigationRoutes("dosen_home")
    data object KaprodiHome : NavigationRoutes("kaprodi_home")
    data object LoginScreen : NavigationRoutes("login")
}