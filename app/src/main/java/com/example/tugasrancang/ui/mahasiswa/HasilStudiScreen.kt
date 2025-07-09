package com.example.tugasrancang.ui.mahasiswa

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tugasrancang.viewmodel.NilaiViewModel
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HasilStudiScreen(
    navController: NavController,
    uid: String,
    viewModel: NilaiViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val hasilStudi by viewModel.hasilStudi.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val df = DecimalFormat("#.##")

    LaunchedEffect(uid) { viewModel.fetchHasilStudi(uid) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hasil Studi per Semester") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Kembali")
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
                hasilStudi.isEmpty() -> Text("Belum ada data hasil studi.")
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        hasilStudi.sortedBy { it.semester }.forEach { semesterData ->
                            item {
                                Text(
                                    semesterData.semester,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "IPS: ${df.format(semesterData.ip)} | IPK: ${df.format(semesterData.ipk)} | Total SKS: ${semesterData.jumlahSks}",
                                    style = MaterialTheme.typography.labelLarge
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                semesterData.detail.forEach { matkul ->
                                    Row(
                                        Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(matkul.namaMatkul, Modifier.weight(1f))
                                        Text("Nilai: ${matkul.nilai} (${matkul.grade})")
                                    }
                                }
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}