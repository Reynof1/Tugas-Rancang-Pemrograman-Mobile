package com.example.tugasrancang.ui.mahasiswa

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tugasrancang.ui.viewmodel.JadwalViewModel
import com.example.tugasrancang.viewmodel.MataKuliah

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JadwalKuliahScreen(
    navController: NavController
) {
    val viewModel: JadwalViewModel = viewModel()
    val jadwalList by viewModel.jadwalList.collectAsState()
    val isLoading   by viewModel.isLoading.collectAsState()

    // Kelompokkan jadwal per hari
    val jadwalPerHari = jadwalList.groupBy { it.hari }

    // Urutan hari pekan
    val hariBerurut = jadwalPerHari.keys.sortedBy {
        when (it.lowercase()) {
            "senin"  -> 1
            "selasa" -> 2
            "rabu"   -> 3
            "kamis"  -> 4
            "jumat"  -> 5
            "sabtu"  -> 6
            else     -> 7
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Jadwal Kuliah") },
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
            when {
                isLoading         -> CircularProgressIndicator()
                jadwalList.isEmpty() -> Text("Tidak ada jadwal kuliah.")
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        hariBerurut.forEach { hari ->
                            item {
                                Text(
                                    text   = hari,
                                    style  = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            items(jadwalPerHari[hari] ?: emptyList()) { matkul ->
                                JadwalCard(matkul = matkul)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun JadwalCard(matkul: MataKuliah) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text       = matkul.namaMatkul,
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = matkul.jam, style = MaterialTheme.typography.bodyMedium)
            Text(
                text  = "Kode: ${matkul.id} | ${matkul.sks} SKS",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
