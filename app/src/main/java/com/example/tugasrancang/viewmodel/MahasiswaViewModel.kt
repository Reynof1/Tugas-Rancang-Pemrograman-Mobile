package com.example.tugasrancang.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// --- MODEL DATA (tetap sama) ---
data class MataKuliahMahasiswa(
    val id: String = "",
    val namaMatkul: String = "",
    val sks: Int = 0,
    val sesiAbsenId: String? = null,
    val sudahAbsen: Boolean = false
)

// --- VIEWMODEL ---
class MahasiswaViewModel : ViewModel() {
    private val database = Firebase.database.reference
    private val auth = Firebase.auth
    private val currentUser = auth.currentUser

    // --- STATES ---
    private val _matkulList = MutableStateFlow<List<MataKuliahMahasiswa>>(emptyList())
    val matkulList = _matkulList.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _mahasiswaName = MutableStateFlow("")
    val mahasiswaName = _mahasiswaName.asStateFlow()

    private val _logoutComplete = MutableStateFlow(false)
    val logoutComplete = _logoutComplete.asStateFlow()

    init {
        fetchMahasiswaName()
        fetchMahasiswaCourses()
    }

    // --- FUNGSI-FUNGSI (Disesuaikan untuk Realtime Database) ---

    private fun fetchMahasiswaName() {
        currentUser ?: return
        database.child("users").child(currentUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _mahasiswaName.value = snapshot.child("nama").getValue(String::class.java) ?: "Mahasiswa"
            }
            override fun onCancelled(error: DatabaseError) { /* Handle error */ }
        })
    }

    private fun fetchMahasiswaCourses() {
        _isLoading.value = true
        currentUser ?: return

        // Karena RTDB tidak bisa query 'array-contains', kita ambil semua matkul lalu filter di aplikasi
        database.child("mataKuliah").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val courses = snapshot.children.mapNotNull { matkulSnapshot ->
                    // Cek apakah UID mahasiswa ada di dalam 'mahasiswaTerdaftar'
                    if (matkulSnapshot.child("mahasiswaTerdaftar").hasChild(currentUser.uid)) {
                        MataKuliahMahasiswa(
                            id = matkulSnapshot.key ?: "",
                            namaMatkul = matkulSnapshot.child("namaMatkul").getValue(String::class.java) ?: "",
                            sks = matkulSnapshot.child("sks").getValue(Int::class.java) ?: 0
                        )
                    } else {
                        null
                    }
                }
                _matkulList.value = courses
                listenToOpenSessions(courses)
            }
            override fun onCancelled(error: DatabaseError) {
                _isLoading.value = false
            }
        })
    }

    private fun listenToOpenSessions(courses: List<MataKuliahMahasiswa>) {
        if (currentUser == null) return
        if (courses.isEmpty()) {
            _isLoading.value = false
            return
        }

        // Dengarkan semua sesi yang terbuka
        database.child("sesiAbsen").orderByChild("status").equalTo("terbuka")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(sessionSnapshot: DataSnapshot) {
                    val openSessions = sessionSnapshot.children.associate {
                        it.child("kodeKelas").getValue(String::class.java) to it
                    }

                    val updatedList = _matkulList.value.map { matkul ->
                        val activeSession = openSessions[matkul.id]
                        val sudahAbsen = activeSession?.child("mahasiswaHadir")?.hasChild(currentUser.uid) ?: false

                        matkul.copy(
                            sesiAbsenId = activeSession?.key,
                            sudahAbsen = sudahAbsen
                        )
                    }
                    _matkulList.value = updatedList
                    _isLoading.value = false
                }
                override fun onCancelled(error: DatabaseError) {
                    _isLoading.value = false
                }
            })
    }

    fun doAbsen(sesiId: String) {
        currentUser ?: return

        val absensiData = mapOf(
            "nama" to _mahasiswaName.value,
            "waktuAbsen" to ServerValue.TIMESTAMP
        )

        // Tambahkan data absen di bawah node mahasiswaHadir/{UID_MAHASISWA}
        database.child("sesiAbsen").child(sesiId)
            .child("mahasiswaHadir").child(currentUser.uid)
            .setValue(absensiData)
    }

    fun logout() {
        auth.signOut()
        _logoutComplete.value = true
    }

    fun onLogoutComplete() {
        _logoutComplete.value = false
    }
}