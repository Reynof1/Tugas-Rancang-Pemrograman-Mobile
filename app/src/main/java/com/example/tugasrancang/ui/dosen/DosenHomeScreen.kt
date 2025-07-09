package com.example.tugasrancang.ui.dosen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.tugasrancang.ui.MenuItem
import com.example.tugasrancang.viewmodel.DosenViewModel
import com.example.tugasrancang.viewmodel.MataKuliah
import com.example.tugasrancang.viewmodel.NavigationRoutes
import kotlinx.coroutines.launch


val dosenMenuItems = listOf(
    MenuItem(NavigationRoutes.DosenHome.route, "Beranda", Icons.Default.Home),
    MenuItem("dosen_pilih_matkul_presensi", "Presensi", Icons.Default.CoPresent),
    MenuItem("dosen_pilih_matkul_riwayat", "Riwayat Presensi", Icons.Default.History),
    MenuItem("dosen_pilih_matkul_nilai", "Input Nilai Mahasiswa", Icons.Default.Edit)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DosenHomeScreen(
    navController: NavController,
    viewModel: DosenViewModel = viewModel()
) {
    val matkulList    by viewModel.matkulList.collectAsState()
    val isLoading     by viewModel.isLoading.collectAsState()
    val dosenName     by viewModel.dosenName.collectAsState()
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
                currentRoute = NavigationRoutes.DosenHome.route,
                menuItems = dosenMenuItems,
                onLogout = { viewModel.logout() },
                closeDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Halo, $dosenName") },
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
                                MatkulCard(matkul = matkul) {
                                    navController.navigate("dosen_sesi/${matkul.id}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatkulCard(
    matkul: MataKuliah,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = matkul.namaMatkul,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "${matkul.hari}, ${matkul.jam}")
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Kode: ${matkul.id} | ${matkul.sks} SKS",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
