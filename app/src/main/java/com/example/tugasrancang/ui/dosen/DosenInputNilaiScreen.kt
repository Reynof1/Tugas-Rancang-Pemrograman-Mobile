package com.example.tugasrancang.ui.dosen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


data class MahasiswaNilai(
    val uid: String,
    val nama: String,
    var nilai: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DosenInputNilaiScreen(
    navController: NavController,
    kodeKelas: String
) {
    val database = Firebase.database.reference
    val context = LocalContext.current

    var mahasiswaList by remember { mutableStateOf<List<MahasiswaNilai>?>(null) }
    var isLoading     by remember { mutableStateOf(true) }
    var errorMsg      by remember { mutableStateOf<String?>(null) }
    var isSaving      by remember { mutableStateOf(false) }

    // Defensive fetch
    LaunchedEffect(kodeKelas) {
        if (kodeKelas.isBlank()) {
            mahasiswaList = emptyList()
            isLoading = false
            errorMsg = "Kode kelas tidak valid."
            return@LaunchedEffect
        }
        isLoading = true
        errorMsg = null
        database.child("mataKuliah").child(kodeKelas).child("mahasiswaTerdaftar")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val uids = snapshot.children.mapNotNull { it.key }
                    if (uids.isEmpty()) {
                        mahasiswaList = emptyList()
                        isLoading = false
                        return
                    }
                    database.child("users")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(usersSnap: DataSnapshot) {
                                val list = uids.map { uid ->
                                    val nama = usersSnap.child(uid).child("nama").getValue(String::class.java) ?: uid
                                    MahasiswaNilai(uid, nama)
                                }
                                mahasiswaList = list
                                isLoading = false
                            }

                            override fun onCancelled(error: DatabaseError) {
                                mahasiswaList = uids.map { MahasiswaNilai(it, it) }
                                isLoading = false
                            }
                        })
                }

                override fun onCancelled(error: DatabaseError) {
                    mahasiswaList = emptyList()
                    isLoading = false
                    errorMsg = "DB error: ${error.message}"
                }
            })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Input Nilai - $kodeKelas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                isLoading -> CircularProgressIndicator()
                errorMsg != null -> Text(errorMsg!!)
                mahasiswaList == null -> Text("Tidak ada data mahasiswa.")
                mahasiswaList!!.isEmpty() -> Text("Tidak ada mahasiswa terdaftar pada kelas ini.")
                else -> {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Input Nilai Mahasiswa", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))

                        mahasiswaList!!.forEachIndexed { idx, mhs ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${idx + 1}. ${mhs.nama}", modifier = Modifier.weight(1f))
                                OutlinedTextField(
                                    value = mhs.nilai,
                                    onValueChange = { newValue ->
                                        mahasiswaList = mahasiswaList!!.toMutableList().also { list ->
                                            list[idx] = list[idx].copy(nilai = newValue)
                                        }
                                    },
                                    modifier = Modifier.width(80.dp),
                                    singleLine = true,
                                    label = { Text("Nilai") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {
                                isSaving = true
                                val nilaiMap = mahasiswaList!!.associate { it.uid to it.nilai }
                                database.child("mataKuliah").child(kodeKelas).child("nilaiMahasiswa")
                                    .setValue(nilaiMap)
                                    .addOnSuccessListener {
                                        isSaving = false
                                        Toast.makeText(context, "Nilai berhasil disimpan", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        isSaving = false
                                        Toast.makeText(context, "Gagal menyimpan nilai", Toast.LENGTH_SHORT).show()
                                    }
                            },
                            enabled = !isSaving
                        ) {
                            if (isSaving) CircularProgressIndicator(Modifier.size(20.dp))
                            else Text("Simpan Semua Nilai")
                        }
                    }
                }
            }
        }
    }
}
