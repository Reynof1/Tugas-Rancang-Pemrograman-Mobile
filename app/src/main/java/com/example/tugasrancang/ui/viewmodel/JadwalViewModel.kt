package com.example.tugasrancang.ui.viewmodel

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



class JadwalViewModel : ViewModel() {
    private val database = Firebase.database.reference
    private val auth = Firebase.auth
    private val currentUser = auth.currentUser

    private val _jadwalList = MutableStateFlow<List<MataKuliah>>(emptyList())
    val jadwalList = _jadwalList.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    init {
        fetchJadwalKuliah()
    }

    private fun fetchJadwalKuliah() {
        _isLoading.value = true
        currentUser ?: return

        database.child("mataKuliah").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val courses = snapshot.children.mapNotNull { matkulSnapshot ->
                    if (matkulSnapshot.child("mahasiswaTerdaftar").hasChild(currentUser.uid)) {
                        MataKuliah(
                            id = matkulSnapshot.key ?: "",
                            namaMatkul = matkulSnapshot.child("namaMatkul").getValue(String::class.java) ?: "",
                            sks = matkulSnapshot.child("sks").getValue(Int::class.java) ?: 0,
                            hari = matkulSnapshot.child("hari").getValue(String::class.java) ?: "N/A",
                            jam = matkulSnapshot.child("jam").getValue(String::class.java) ?: "N/A"
                        )
                    } else {
                        null
                    }
                }
                // Urutkan berdasarkan urutan hari standar
                val comparator = compareBy<MataKuliah> {
                    when (it.hari.lowercase()) {
                        "senin" -> 1
                        "selasa" -> 2
                        "rabu" -> 3
                        "kamis" -> 4
                        "jumat" -> 5
                        "sabtu" -> 6
                        else -> 7
                    }
                }
                _jadwalList.value = courses.sortedWith(comparator)
                _isLoading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                _isLoading.value = false
            }
        })
    }
}