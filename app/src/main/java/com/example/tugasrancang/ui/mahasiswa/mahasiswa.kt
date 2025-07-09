package com.example.tugasrancang.ui.mahasiswa

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tugasrancang.ui.viewmodel.KrsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KrsScreen(
    navController: NavController,
    viewModel: KrsViewModel = viewModel()
) {
    val semuaMatkul by viewModel.semuaMatkulList.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Isi Kartu Rencana Studi") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    viewModel.simpanKrs()
                    Toast.makeText(context, "KRS Disimpan!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Simpan KRS")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(semuaMatkul) { matkul ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = viewModel.pilihanKrs[matkul.id] ?: false,
                        onCheckedChange = { isChecked ->
                            viewModel.pilihanKrs[matkul.id] = isChecked
                        }
                    )
                    Text(
                        text = "${matkul.namaMatkul} (${matkul.sks} SKS)",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}
