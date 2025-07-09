package com.example.tugasrancang.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class DosenInfo(
    val uid: String = "",
    val nama: String = ""
)

class KaprodiViewModel : ViewModel() {
    private val database = Firebase.database.reference
    private val auth = Firebase.auth
    private val currentUser = auth.currentUser

    private val _dosenList = MutableStateFlow<List<DosenInfo>>(emptyList())
    val dosenList = _dosenList.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage = _toastMessage.asStateFlow()

    private val _kaprodiName = MutableStateFlow("")
    val kaprodiName = _kaprodiName.asStateFlow()

    private val _logoutComplete = MutableStateFlow(false)
    val logoutComplete = _logoutComplete.asStateFlow()

    init {
        fetchDosenList()
        fetchKaprodiName()
    }

    private fun fetchKaprodiName() {
        currentUser ?: return
        database.child("users").child(currentUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _kaprodiName.value = snapshot.child("nama").getValue(String::class.java) ?: "Kaprodi"
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun fetchDosenList() {
        database.child("users").orderByChild("role").equalTo("dosen").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { userSnapshot ->
                    DosenInfo(
                        uid = userSnapshot.key ?: "",
                        nama = userSnapshot.child("nama").getValue(String::class.java) ?: "Dosen tidak dikenal"
                    )
                }
                _dosenList.value = list
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun addMataKuliah(
        kode: String, nama: String, sks: String, dosenId: String,
        hari: String, jam: String
    ) {
        if (kode.isBlank() || nama.isBlank() || sks.isBlank() || dosenId.isBlank() || hari.isBlank() || jam.isBlank()) {
            _toastMessage.value = "Semua field harus diisi"
            return
        }
        val mataKuliahData = mapOf(
            "namaMatkul" to nama,
            "sks" to sks.toIntOrNull(),
            "dosenPengampuId" to dosenId,
            "hari" to hari,
            "jam" to jam,
            "mahasiswaTerdaftar" to mapOf<String, Boolean>()
        )
        database.child("mataKuliah").child(kode).setValue(mataKuliahData)
            .addOnSuccessListener {
                _toastMessage.value = "Mata kuliah berhasil ditambahkan"
            }
            .addOnFailureListener {
                _toastMessage.value = "Error: Gagal menambahkan data"
            }
    }

    fun onToastShown() {
        _toastMessage.value = null
    }

    fun logout() {
        auth.signOut()
        _logoutComplete.value = true
    }

    fun onLogoutComplete() {
        _logoutComplete.value = false
    }
}