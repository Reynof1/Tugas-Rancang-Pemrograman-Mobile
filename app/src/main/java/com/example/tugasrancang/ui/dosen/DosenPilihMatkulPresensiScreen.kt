package com.example.tugasrancang.ui.dosen

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tugasrancang.viewmodel.DosenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DosenPilihMatkulPresensiScreen(
    navController: NavController,
    viewModel: DosenViewModel = viewModel()
) {
    val matkulList by viewModel.matkulList.collectAsState()
    val isLoading  by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pilih Matakuliah untuk Presensi") },
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
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(matkulList) { matkul ->
                        // Menggunakan kembali Composable MatkulCard dari DosenHomeScreen
                        MatkulCard(matkul = matkul, onClick = {
                            // Navigasi ke layar sesi presensi yang sebenarnya
                            navController.navigate("dosen_sesi/${matkul.id}")
                        })
                    }
                }
            }
        }
    }
}
