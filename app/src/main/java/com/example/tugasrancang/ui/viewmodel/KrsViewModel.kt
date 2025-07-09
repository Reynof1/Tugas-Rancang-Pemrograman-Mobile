package com.example.tugasrancang.ui.viewmodel

import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import com.example.tugasrancang.viewmodel.MataKuliah
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class KrsViewModel : ViewModel() {
    private val database = Firebase.database.reference
    private val auth = Firebase.auth
    private val currentUser = auth.currentUser

    private val _semuaMatkulList = MutableStateFlow<List<MataKuliah>>(emptyList())
    val semuaMatkulList = _semuaMatkulList.asStateFlow()

    // State untuk menyimpan pilihan mahasiswa
    val pilihanKrs = mutableStateMapOf<String, Boolean>()

    init {
        fetchSemuaMataKuliah()
    }

    private fun fetchSemuaMataKuliah() {
        currentUser ?: return

        // 1× read – pakai get()
        database.child("mataKuliah").get().addOnSuccessListener { snapshot ->
            val list = snapshot.children.mapNotNull { matkulSnapshot ->
                MataKuliah(
                    id = matkulSnapshot.key ?: "",
                    namaMatkul = matkulSnapshot.child("namaMatkul").getValue(String::class.java) ?: "",
                    sks = matkulSnapshot.child("sks").getValue(Int::class.java) ?: 0
                )
            }
            _semuaMatkulList.value = list

            // Cek mata kuliah yang sudah diambil
            list.forEach { matkul ->
                database.child("mataKuliah")
                    .child(matkul.id)
                    .child("mahasiswaTerdaftar")
                    .child(currentUser.uid)
                    .get()
                    .addOnSuccessListener { userSnap ->
                        if (userSnap.exists()) {
                            pilihanKrs[matkul.id] = true
                        }
                    }
            }
        }
    }

    fun simpanKrs() {
        currentUser ?: return
        pilihanKrs.forEach { (matkulId, isChecked) ->
            val matkulRef = database.child("mataKuliah")
                .child(matkulId)
                .child("mahasiswaTerdaftar")
                .child(currentUser.uid)

            if (isChecked) {
                matkulRef.setValue(true)   // Tambahkan mahasiswa
            } else {
                matkulRef.removeValue()    // Hapus mahasiswa
            }
        }
    }
}
