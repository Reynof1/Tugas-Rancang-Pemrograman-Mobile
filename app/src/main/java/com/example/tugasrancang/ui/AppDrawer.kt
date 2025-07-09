package com.example.tugasrancang.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.tugasrancang.viewmodel.NavigationRoutes

data class MenuItem(
    val route: String,
    val title: String,
    val icon: ImageVector
)

val mahasiswaMenuItems = listOf(
    MenuItem(NavigationRoutes.MahasiswaHome.route, "Beranda", Icons.Default.Home),
    MenuItem("isi_krs", "Isi KRS", Icons.Default.Edit),
    MenuItem("jadwal_kuliah", "Jadwal Kuliah", Icons.Default.CalendarToday),
    MenuItem("kartu_studi", "Kartu Studi", Icons.Default.CreditCard),
    MenuItem("hasil_studi", "Hasil Studi", Icons.Default.School),
    MenuItem("transkrip_nilai", "Transkrip Nilai", Icons.Default.Article)
)

@Composable
fun AppDrawer(
    navController: NavController,
    currentRoute: String,
    menuItems: List<MenuItem>,
    onLogout: () -> Unit,
    closeDrawer: () -> Unit
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("SIASAT Mobile", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))
            Divider()
        }
        menuItems.forEach { item ->
            NavigationDrawerItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                        }
                    }
                    closeDrawer()
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
        Spacer(Modifier.weight(1f))
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.Logout, contentDescription = "Logout") },
            label = { Text("Logout") },
            selected = false,
            onClick = onLogout
        )
    }
}