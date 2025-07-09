package com.example.tugasrancang.ui.kaprodi

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tugasrancang.ui.AppDrawer
import com.example.tugasrancang.ui.MenuItem
import com.example.tugasrancang.viewmodel.DosenInfo
import com.example.tugasrancang.viewmodel.KaprodiViewModel
import com.example.tugasrancang.viewmodel.NavigationRoutes
import kotlinx.coroutines.launch

val kaprodiMenuItems = listOf(
    MenuItem(NavigationRoutes.KaprodiHome.route, "Tambah Matakuliah", Icons.Default.AddCircle)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KaprodiHomeScreen(
    navController: NavController,
    viewModel: KaprodiViewModel = viewModel()
) {
    var kode by remember { mutableStateOf("") }
    var nama by remember { mutableStateOf("") }
    var sks by remember { mutableStateOf("") }
    var selectedDosen by remember { mutableStateOf<DosenInfo?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val hariPilihan = listOf("Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
    var selectedHari by remember { mutableStateOf(hariPilihan.first()) }
    var isHariDropdownExpanded by remember { mutableStateOf(false) }
    var jam by remember { mutableStateOf("") }

    val dosenList by viewModel.dosenList.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val kaprodiName by viewModel.kaprodiName.collectAsState()
    val logoutComplete by viewModel.logoutComplete.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(logoutComplete) {
        if (logoutComplete) {
            navController.navigate(NavigationRoutes.LoginScreen.route) {
                popUpTo(navController.graph.id) { inclusive = true }
            }
            viewModel.onLogoutComplete()
        }
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.onToastShown()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                navController = navController,
                currentRoute = NavigationRoutes.KaprodiHome.route,
                menuItems = kaprodiMenuItems,
                onLogout = { viewModel.logout() },
                closeDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Halo, $kaprodiName") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Form Tambah Mata Kuliah", style = MaterialTheme.typography.titleLarge)

                OutlinedTextField(
                    value = kode,
                    onValueChange = { kode = it },
                    label = { Text("Kode Mata Kuliah (cth: TI101)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Nama Mata Kuliah") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = sks,
                    onValueChange = { sks = it },
                    label = { Text("Jumlah SKS") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // Pilih Hari Perkuliahan
                ExposedDropdownMenuBox(
                    expanded = isHariDropdownExpanded,
                    onExpandedChange = { isHariDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedHari,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isHariDropdownExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        label = { Text("Hari Perkuliahan") }
                    )
                    ExposedDropdownMenu(
                        expanded = isHariDropdownExpanded,
                        onDismissRequest = { isHariDropdownExpanded = false }
                    ) {
                        hariPilihan.forEach { hari ->
                            DropdownMenuItem(
                                text = { Text(hari) },
                                onClick = {
                                    selectedHari = hari
                                    isHariDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = jam,
                    onValueChange = { jam = it },
                    label = { Text("Jam (cth: 08:00-09:40)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Dropdown dosen pengampu
                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedDosen?.nama ?: "Pilih Dosen Pengampu",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        label = { Text("Dosen Pengampu") }
                    )
                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        dosenList.forEach { dosen ->
                            DropdownMenuItem(
                                text = { Text(dosen.nama) },
                                onClick = {
                                    selectedDosen = dosen
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        viewModel.addMataKuliah(
                            kode,
                            nama,
                            sks,
                            selectedDosen?.uid ?: "",
                            selectedHari,
                            jam
                        )
                        kode = ""
                        nama = ""
                        sks = ""
                        selectedDosen = null
                        selectedHari = hariPilihan.first()
                        jam = ""
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Simpan Mata Kuliah")
                }
            }
        }
    }
}
