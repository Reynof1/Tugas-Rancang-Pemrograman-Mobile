package com.example.tugasrancang.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tugasrancang.model.HasilStudiSemester
import com.example.tugasrancang.model.TranskripMatkul
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NilaiViewModel : ViewModel() {

    /* ------------------ State ------------------ */
    private val _hasilStudi = MutableStateFlow<List<HasilStudiSemester>>(emptyList())
    val hasilStudi: StateFlow<List<HasilStudiSemester>> = _hasilStudi

    private val _flatNilai = MutableStateFlow<List<TranskripMatkul>>(emptyList())
    // alias agar nama "transkrip" masih bisa dipakai di UI lama
    val transkrip: StateFlow<List<TranskripMatkul>> get() = _flatNilai
    val flatNilai: StateFlow<List<TranskripMatkul>> get() = _flatNilai

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /* ------------------ Public API -------------- */
    fun fetchAll(uidParam: String? = null) {
        val uid = uidParam ?: FirebaseAuth.getInstance().currentUser?.uid ?: return
        _isLoading.value = true

        val dbRef = FirebaseDatabase.getInstance().reference
        dbRef.child("hasilStudi").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val semesterList = mutableListOf<HasilStudiSemester>()
                    val flatList = mutableListOf<TranskripMatkul>()

                    for (semSnap in snapshot.children) {
                        val semester = semSnap.key ?: continue
                        val jumlahSks = semSnap.child("jumlahSks").getValue(Int::class.java) ?: 0
                        val ip = semSnap.child("ip").getValue(Double::class.java) ?: 0.0
                        val ipk = semSnap.child("ipk").getValue(Double::class.java) ?: 0.0

                        val detailList = mutableListOf<TranskripMatkul>()
                        for (matkulSnap in semSnap.child("detail").children) {
                            val kode = matkulSnap.key ?: ""
                            val nama = matkulSnap.child("namaMatkul").getValue(String::class.java) ?: ""
                            val sks = matkulSnap.child("sks").getValue(Int::class.java) ?: 0
                            val nilai = matkulSnap.child("nilai").getValue(Int::class.java) ?: 0
                            val grade = matkulSnap.child("grade").getValue(String::class.java) ?: ""

                            val item = TranskripMatkul(
                                kodeMatkul = kode,
                                namaMatkul = nama,
                                sks = sks,
                                nilai = nilai,
                                grade = grade,
                                semester = semester
                            )
                            detailList += item
                            flatList   += item
                        }
                        semesterList += HasilStudiSemester(
                            semester   = semester,
                            jumlahSks  = jumlahSks,
                            ip         = ip,
                            ipk        = ipk,
                            detail     = detailList
                        )
                    }

                    _hasilStudi.value = semesterList
                    _flatNilai.value = flatList
                    _isLoading.value = false
                }

                override fun onCancelled(error: DatabaseError) {
                    _hasilStudi.value = emptyList()
                    _flatNilai.value = emptyList()
                    _isLoading.value = false
                }
            })
    }

    // --- Aliases agar UI lama tidak perlu diubah ---
    fun fetchHasilStudi(uid: String)  = fetchAll(uid)
    fun fetchTranskrip(uid: String)   = fetchAll(uid)
}
