package com.example.tugasrancang.ui.dosen

import androidx.compose.foundation.clickable
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
import com.example.tugasrancang.viewmodel.RiwayatSesi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiwayatPresensiScreen(
    navController: NavController,
    kodeKelas: String,
    viewModel: DosenViewModel = viewModel()
) {
    val riwayatList by viewModel.riwayatSesiList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(key1 = kodeKelas) {
        viewModel.fetchRiwayatSesi(kodeKelas)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Riwayat - $kodeKelas") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else if (riwayatList.isEmpty()) {
                Text("Belum ada riwayat presensi untuk kelas ini.")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(riwayatList) { sesi ->
                        RiwayatSesiCard(sesi = sesi)
                    }
                }
            }
        }
    }
}

@Composable
fun RiwayatSesiCard(sesi: RiwayatSesi) {
    var isExpanded by remember { mutableStateOf(false) }
    val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy, HH:mm", Locale.getDefault())
    val tanggal = sdf.format(Date(sesi.waktuBuka))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = tanggal, fontWeight = FontWeight.Bold)
            Text(text = "Jumlah Hadir: ${sesi.mahasiswaHadir.size} mahasiswa")

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Text("Daftar Kehadiran:", fontWeight = FontWeight.SemiBold)
                sesi.mahasiswaHadir.values.forEach { mahasiswa ->
                    val namaMahasiswa = (mahasiswa as? Map<*, *>)?.get("nama") ?: "Tanpa Nama"
                    Text("- $namaMahasiswa")
                }
            }
        }
    }
}
