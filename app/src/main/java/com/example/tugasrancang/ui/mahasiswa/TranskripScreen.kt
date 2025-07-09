package com.example.tugasrancang.ui.mahasiswa

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
import androidx.navigation.NavController
import com.example.tugasrancang.viewmodel.NilaiViewModel
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranskripScreen(
    navController: NavController,
    uid: String,
    viewModel: NilaiViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val transkrip  by viewModel.transkrip.collectAsState()
    val isLoading  by viewModel.isLoading.collectAsState()

    LaunchedEffect(uid) { viewModel.fetchTranskrip(uid) }

    val totalSks   = transkrip.sumOf { it.sks }
    val totalBobot = transkrip.sumOf { it.sks * it.nilai }
    val ipk        = if (totalSks == 0) 0.0 else totalBobot.toDouble() / totalSks
    val df = DecimalFormat("#.##")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transkrip Nilai") },
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
                transkrip.isEmpty() -> Text("Belum ada data transkrip nilai.")
                else -> {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            Text("Total SKS: $totalSks", fontWeight = FontWeight.Bold)
                            Text("IPK: ${df.format(ipk)}", fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(16.dp))
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            item {
                                Row {
                                    Text("Semester", Modifier.width(80.dp), fontWeight = FontWeight.Bold)
                                    Text("Mata Kuliah", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                                    Text("SKS", Modifier.width(40.dp), fontWeight = FontWeight.Bold)
                                    Text("Nilai", Modifier.width(40.dp), fontWeight = FontWeight.Bold)
                                    Text("Grade", Modifier.width(50.dp), fontWeight = FontWeight.Bold)
                                }
                                Divider()
                            }
                            items(transkrip.sortedWith(compareBy({ it.semester }, { it.kodeMatkul }))) { nilai ->
                                Row {
                                    Text(nilai.semester, Modifier.width(80.dp))
                                    Text(nilai.namaMatkul, Modifier.weight(1f))
                                    Text(nilai.sks.toString(), Modifier.width(40.dp))
                                    Text(nilai.nilai.toString(), Modifier.width(40.dp))
                                    Text(nilai.grade, Modifier.width(50.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
