package com.example.tugasrancang.ui.dosen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
fun DosenPilihMatkulRiwayatScreen(
    navController: NavController,
    openDrawer: () -> Unit,
    viewModel: DosenViewModel = viewModel()
) {
    val matkulList by viewModel.matkulList.collectAsState()
    val isLoading  by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pilih Matakuliah") },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
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
                isLoading -> CircularProgressIndicator()
                matkulList.isEmpty() -> Text("Anda tidak mengampu mata kuliah apapun.")
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(matkulList) { matkul ->
                            MatkulCard(matkul = matkul, onClick = {
                                navController.navigate("riwayat_presensi/${matkul.id}")
                            })
                        }
                    }
                }
            }
        }
    }
}
