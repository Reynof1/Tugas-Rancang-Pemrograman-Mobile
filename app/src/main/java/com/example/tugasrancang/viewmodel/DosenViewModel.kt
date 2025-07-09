package com.example.tugasrancang.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*

// --- MODEL DATA ---
data class MataKuliah(
    val id: String = "",
    val namaMatkul: String = "",
    val sks: Int = 0,
    val hari: String = "",
    val jam: String = ""
)

data class SesiAbsen(
    val id: String = "",
    val status: String = "tertutup",
    val mahasiswaHadir: Map<String, Map<String, Any>> = emptyMap()
)

data class RiwayatSesi(
    val id: String = "",
    val waktuBuka: Long = 0L,
    val mahasiswaHadir: Map<String, Map<String, Any>> = emptyMap()
)

// --- VIEWMODEL ---
class DosenViewModel : ViewModel() {
    private val database = Firebase.database.reference
    private val auth = Firebase.auth
    private val currentUser = auth.currentUser

    // --- STATES ---
    private val _matkulList = MutableStateFlow<List<MataKuliah>>(emptyList())
    val matkulList = _matkulList.asStateFlow()

    private val _dosenName = MutableStateFlow("")
    val dosenName = _dosenName.asStateFlow()

    private val _sesiAktif = MutableStateFlow<SesiAbsen?>(null)
    val sesiAktif = _sesiAktif.asStateFlow()

    private val _riwayatSesiList = MutableStateFlow<List<RiwayatSesi>>(emptyList())
    val riwayatSesiList = _riwayatSesiList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _logoutComplete = MutableStateFlow(false)
    val logoutComplete = _logoutComplete.asStateFlow()

    private var sessionListener: ValueEventListener? = null
    private var sessionReference: Query? = null

    init {
        fetchDosenName()
        fetchDosenCourses()
    }

    private fun fetchDosenName() {
        currentUser ?: return
        database.child("users").child(currentUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _dosenName.value = snapshot.child("nama").getValue(String::class.java) ?: "Dosen"
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun fetchDosenCourses() {
        _isLoading.value = true
        currentUser ?: return

        database.child("mataKuliah").orderByChild("dosenPengampuId").equalTo(currentUser.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull { matkulSnapshot ->
                        MataKuliah(
                            id = matkulSnapshot.key ?: "",
                            namaMatkul = matkulSnapshot.child("namaMatkul").getValue(String::class.java) ?: "",
                            sks = matkulSnapshot.child("sks").getValue(Int::class.java) ?: 0,
                            hari = matkulSnapshot.child("hari").getValue(String::class.java) ?: "N/A",
                            jam = matkulSnapshot.child("jam").getValue(String::class.java) ?: "N/A"
                        )
                    }
                    _matkulList.value = list
                    _isLoading.value = false
                }
                override fun onCancelled(error: DatabaseError) { _isLoading.value = false }
            })
    }

    fun fetchRiwayatSesi(kodeKelas: String) {
        _isLoading.value = true
        database.child("sesiAbsen").orderByChild("kodeKelas").equalTo(kodeKelas)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull { sesiSnapshot ->
                        RiwayatSesi(
                            id = sesiSnapshot.key ?: "",
                            waktuBuka = sesiSnapshot.child("waktuBuka").getValue(Long::class.java) ?: 0L,
                            mahasiswaHadir = sesiSnapshot.child("mahasiswaHadir").getValue() as? Map<String, Map<String, Any>> ?: emptyMap()
                        )
                    }
                    _riwayatSesiList.value = list.sortedByDescending { it.waktuBuka }
                    _isLoading.value = false
                }
                override fun onCancelled(error: DatabaseError) { _isLoading.value = false }
            })
    }

    fun monitorSesi(kodeKelas: String) {
        sessionListener?.let { sessionReference?.removeEventListener(it) }
        sessionReference = database.child("sesiAbsen").orderByChild("kodeKelas").equalTo(kodeKelas)
        sessionListener = sessionReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val activeSessionSnapshot = snapshot.children.firstOrNull {
                    it.child("status").getValue(String::class.java) == "terbuka"
                }
                if (activeSessionSnapshot != null) {
                    _sesiAktif.value = SesiAbsen(
                        id = activeSessionSnapshot.key ?: "",
                        status = "terbuka",
                        mahasiswaHadir = activeSessionSnapshot.child("mahasiswaHadir").getValue() as? Map<String, Map<String, Any>> ?: emptyMap()
                    )
                } else {
                    _sesiAktif.value = null
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun bukaSesiAbsen(kodeKelas: String) {
        val sesiId = database.child("sesiAbsen").push().key ?: return
        val sesiBaru = mapOf(
            "kodeKelas" to kodeKelas,
            "dosenId" to (currentUser?.uid ?: ""),
            "waktuBuka" to ServerValue.TIMESTAMP,
            "status" to "terbuka"
        )
        database.child("sesiAbsen").child(sesiId).setValue(sesiBaru)
    }

    fun tutupSesi(sesiId: String) {
        database.child("sesiAbsen").child(sesiId).child("status").setValue("tertutup")
    }

    fun logout() {
        auth.signOut()
        _logoutComplete.value = true
    }

    fun onLogoutComplete() {
        _logoutComplete.value = false
    }

    override fun onCleared() {
        super.onCleared()
        sessionListener?.let { sessionReference?.removeEventListener(it) }
    }
}