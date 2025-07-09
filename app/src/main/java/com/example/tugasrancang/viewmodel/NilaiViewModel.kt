package com.example.tugasrancang.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tugasrancang.model.HasilStudiSemester
import com.example.tugasrancang.model.TranskripMatkul
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NilaiViewModel : ViewModel() {
    private val _transkrip = MutableStateFlow<List<TranskripMatkul>>(emptyList())
    val transkrip: StateFlow<List<TranskripMatkul>> = _transkrip

    private val _hasilStudi = MutableStateFlow<List<HasilStudiSemester>>(emptyList())
    val hasilStudi: StateFlow<List<HasilStudiSemester>> = _hasilStudi

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchTranskrip(uid: String) {
        _isLoading.value = true
        val db = FirebaseDatabase.getInstance().reference
        db.child("transkripNilai").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val result = mutableListOf<TranskripMatkul>()
                    for (semesterSnap in snapshot.children) {
                        val semester = semesterSnap.key ?: ""
                        for (matkulSnap in semesterSnap.children) {
                            val kode = matkulSnap.key ?: ""
                            val nama = matkulSnap.child("namaMatkul").getValue(String::class.java) ?: ""
                            val sks = matkulSnap.child("sks").getValue(Int::class.java) ?: 0
                            val nilai = matkulSnap.child("nilai").getValue(Int::class.java) ?: 0
                            val grade = matkulSnap.child("grade").getValue(String::class.java) ?: ""
                            result.add(
                                TranskripMatkul(
                                    kodeMatkul = kode, namaMatkul = nama, sks = sks,
                                    nilai = nilai, grade = grade, semester = semester
                                )
                            )
                        }
                    }
                    _transkrip.value = result
                    _isLoading.value = false
                }
                override fun onCancelled(error: DatabaseError) {
                    _transkrip.value = emptyList()
                    _isLoading.value = false
                }
            })
    }

    fun fetchHasilStudi(uid: String) {
        _isLoading.value = true
        val db = FirebaseDatabase.getInstance().reference
        db.child("hasilStudi").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val result = mutableListOf<HasilStudiSemester>()
                    for (semesterSnap in snapshot.children) {
                        val semester = semesterSnap.key ?: ""
                        val jumlahSks = semesterSnap.child("jumlahSks").getValue(Int::class.java) ?: 0
                        val ip = semesterSnap.child("ip").getValue(Double::class.java) ?: 0.0
                        val ipk = semesterSnap.child("ipk").getValue(Double::class.java) ?: 0.0
                        val detailList = mutableListOf<TranskripMatkul>()
                        val detailSnap = semesterSnap.child("detail")
                        for (matkulSnap in detailSnap.children) {
                            val kode = matkulSnap.key ?: ""
                            val nama = matkulSnap.child("namaMatkul").getValue(String::class.java) ?: ""
                            val sks = matkulSnap.child("sks").getValue(Int::class.java) ?: 0
                            val nilai = matkulSnap.child("nilai").getValue(Int::class.java) ?: 0
                            val grade = matkulSnap.child("grade").getValue(String::class.java) ?: ""
                            detailList.add(
                                TranskripMatkul(
                                    kodeMatkul = kode, namaMatkul = nama, sks = sks,
                                    nilai = nilai, grade = grade, semester = semester
                                )
                            )
                        }
                        result.add(
                            HasilStudiSemester(
                                semester = semester, jumlahSks = jumlahSks,
                                ip = ip, ipk = ipk, detail = detailList
                            )
                        )
                    }
                    _hasilStudi.value = result
                    _isLoading.value = false
                }
                override fun onCancelled(error: DatabaseError) {
                    _hasilStudi.value = emptyList()
                    _isLoading.value = false
                }
            })
    }
}