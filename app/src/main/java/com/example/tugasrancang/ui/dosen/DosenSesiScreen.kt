package com.example.tugasrancang.ui.dosen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tugasrancang.viewmodel.DosenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DosenSesiScreen(
    navController: NavController,
    kodeKelas: String,
    viewModel: DosenViewModel = viewModel()
) {
    val sesiAktif by viewModel.sesiAktif.collectAsState()
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var goBack by remember { mutableStateOf(false) }

    LaunchedEffect(kodeKelas) {
        viewModel.monitorSesi(kodeKelas)
    }

    // Jika goBack true, langsung kembali ke halaman sebelumnya.
    LaunchedEffect(goBack) {
        if (goBack) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kelola Sesi - $kodeKelas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            errorMsg?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            if (sesiAktif == null) {
                Button(onClick = { viewModel.bukaSesiAbsen(kodeKelas) }) {
                    Text("Buka Sesi Presensi")
                }
            } else {
                Text(
                    "Sesi Sedang Terbuka",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = {
                        val sesiId = sesiAktif?.id
                        if (!sesiId.isNullOrBlank()) {
                            viewModel.tutupSesi(sesiId)
                            goBack = true // trigger langsung kembali
                        } else {
                            errorMsg = "ID sesi tidak ditemukan. Tidak dapat menutup sesi."
                        }
                    }
                ) {
                    Text("Tutup Sesi Presensi")
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Mahasiswa Hadir: ${sesiAktif?.mahasiswaHadir?.size ?: 0}",
                    fontWeight = FontWeight.Bold
                )

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(sesiAktif?.mahasiswaHadir?.values?.toList() ?: emptyList()) { mahasiswa ->
                        val namaMahasiswa = (mahasiswa as? Map<*, *>)?.get("nama") ?: "Tanpa Nama"
                        Text("- $namaMahasiswa")
                    }
                }
            }

            Divider()

            Button(
                onClick = { navController.navigate("riwayat_presensi/$kodeKelas") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Lihat Riwayat Presensi")
            }
        }
    }
}
