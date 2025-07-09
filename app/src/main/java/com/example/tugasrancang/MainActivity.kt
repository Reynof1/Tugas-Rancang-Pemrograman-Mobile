package com.example.tugasrancang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tugasrancang.ui.dosen.*
import com.example.tugasrancang.ui.kaprodi.KaprodiHomeScreen
import com.example.tugasrancang.ui.login.LoginScreen
import com.example.tugasrancang.ui.mahasiswa.*
import com.example.tugasrancang.ui.theme.TugasRancangTheme
import com.example.tugasrancang.viewmodel.LoginViewModel
import com.example.tugasrancang.viewmodel.NavigationRoutes
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition { loginViewModel.isLoading.value }
        }

        setContent {
            TugasRancangTheme {
                val navController = rememberNavController()

                // Navigasi otomatis setelah login berdasarkan role
                LaunchedEffect(Unit) {
                    loginViewModel.navigationState.collect { navRoute ->
                        navRoute?.let {
                            navController.navigate(it.route) {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                            loginViewModel.onNavigationComplete()
                        }
                    }
                }

                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                NavHost(
                    navController = navController,
                    startDestination = NavigationRoutes.LoginScreen.route
                ) {
                    // ---------- AUTH ----------
                    composable(NavigationRoutes.LoginScreen.route) {
                        LoginScreen(navController, loginViewModel)
                    }

                    // ---------- MAHASISWA ----------
                    composable(NavigationRoutes.MahasiswaHome.route) {
                        MahasiswaHomeScreen(navController)
                    }
                    composable("isi_krs")         { KrsScreen(navController) }
                    composable("jadwal_kuliah")   { JadwalKuliahScreen(navController) }
                    composable("kartu_studi")     { KartuStudiScreen(navController) }
                    composable("hasil_studi")     { HasilStudiScreen(navController, uid) }
                    composable("transkrip_nilai") { TranskripScreen(navController, uid) }

                    // ---------- DOSEN ----------
                    composable(NavigationRoutes.DosenHome.route) {
                        DosenHomeScreen(navController)
                    }
                    composable("dosen_pilih_matkul_presensi") {
                        DosenPilihMatkulPresensiScreen(navController)
                    }
                    composable("dosen_pilih_matkul_riwayat") {
                        DosenPilihMatkulRiwayatScreen(navController, openDrawer = { })
                    }
                    composable("dosen_pilih_matkul_nilai") {
                        DosenPilihMatkulNilaiScreen(navController, openDrawer = { })
                    }
                    composable(
                        "dosen_sesi/{kodeKelas}",
                        arguments = listOf(navArgument("kodeKelas") { type = NavType.StringType })
                    ) { backStack ->
                        val kode = backStack.arguments?.getString("kodeKelas") ?: ""
                        DosenSesiScreen(navController, kode)
                    }
                    composable(
                        "riwayat_presensi/{kodeKelas}",
                        arguments = listOf(navArgument("kodeKelas") { type = NavType.StringType })
                    ) { backStack ->
                        val kode = backStack.arguments?.getString("kodeKelas") ?: ""
                        RiwayatPresensiScreen(navController, kode)
                    }
                    composable(
                        "input_nilai/{kodeKelas}",
                        arguments = listOf(navArgument("kodeKelas") { type = NavType.StringType })
                    ) { backStack ->
                        val kode = backStack.arguments?.getString("kodeKelas") ?: ""
                        DosenInputNilaiScreen(navController, kode)
                    }

                    // ---------- KAPRODI ----------
                    composable(NavigationRoutes.KaprodiHome.route) {
                        KaprodiHomeScreen(navController)
                    }
                }
            }
        }
    }
}
