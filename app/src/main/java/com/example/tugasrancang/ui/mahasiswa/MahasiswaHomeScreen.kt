package com.example.tugasrancang.ui.mahasiswa

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tugasrancang.ui.AppDrawer
import com.example.tugasrancang.ui.mahasiswaMenuItems
import com.example.tugasrancang.viewmodel.MahasiswaViewModel
import com.example.tugasrancang.viewmodel.MataKuliahMahasiswa
import com.example.tugasrancang.viewmodel.NavigationRoutes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MahasiswaHomeScreen(
    navController: NavController,
    viewModel: MahasiswaViewModel = viewModel()
) {
    val matkulList by viewModel.matkulList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val mahasiswaName by viewModel.mahasiswaName.collectAsState()
    val logoutComplete by viewModel.logoutComplete.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(logoutComplete) {
        if (logoutComplete) {
            navController.navigate(NavigationRoutes.LoginScreen.route) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
            viewModel.onLogoutComplete()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                navController = navController,
                currentRoute = NavigationRoutes.MahasiswaHome.route,
                menuItems = mahasiswaMenuItems,
                onLogout = { viewModel.logout() },
                closeDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Halo, $mahasiswaName") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
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
                if (isLoading) {
                    CircularProgressIndicator()
                } else if (matkulList.isEmpty()) {
                    Text("Anda belum mengambil mata kuliah semester ini.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(matkulList) { matkul ->
                            MatkulMahasiswaCard(
                                matkul = matkul,
                                onAbsenClick = {
                                    if (matkul.sesiAbsenId != null) {
                                        viewModel.doAbsen(matkul.sesiAbsenId)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatkulMahasiswaCard(matkul: MataKuliahMahasiswa, onAbsenClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = matkul.namaMatkul,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Kode: ${matkul.id} | ${matkul.sks} SKS",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (matkul.sesiAbsenId != null && !matkul.sudahAbsen) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onAbsenClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Lakukan Absen Sekarang")
                }
            } else if (matkul.sudahAbsen) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Anda Sudah Absen ✅", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
