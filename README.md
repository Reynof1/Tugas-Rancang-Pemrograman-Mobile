# Tugas Rancang Pemrograman Mobile

Aplikasi Android yang digunakan sebagai simulasi sistem informasi akademik dengan peran Mahasiswa, Dosen, dan Kaprodi.

## 📱 Fitur Utama

- Login untuk Mahasiswa, Dosen, dan Kaprodi
- Mahasiswa dapat melihat:
  - Kartu Studi
  - Jadwal Kuliah
  - Hasil Studi
  - Transkrip Nilai
- Dosen dapat:
  - Melihat daftar matkul
  - Input nilai & presensi
  - Lihat riwayat presensi
- Kaprodi memiliki tampilan khusus untuk monitoring akademik

## 🧩 Struktur Folder

com.example.tugasrancang
├── model
│ └── TranskripMatkul.kt
│
├── ui
│ ├── dosen
│ │ ├── DosenHomeScreen.kt
│ │ ├── DosenInputNilaiScreen.kt
│ │ ├── DosenPilihMatkulNilaiScreen.kt
│ │ ├── DosenPilihMatkulPresensiScreen.kt
│ │ ├── DosenPilihMatkulRiwayatScreen.kt
│ │ ├── DosenSesiScreen.kt
│ │ └── RiwayatPresensiScreen.kt
│ │
│ ├── kaprodi
│ │ └── KaprodiHomeScreen.kt
│ │
│ ├── login
│ │ └── LoginScreen.kt
│ │
│ ├── mahasiswa
│ │ ├── HasilStudiScreen.kt
│ │ ├── JadwalKuliahScreen.kt
│ │ ├── KartuStudiScreen.kt
│ │ ├── MahasiswaHomeScreen.kt
│ │ ├── TranskripScreen.kt
│ │ └── mahasiswa.kt
│ │
│ ├── theme
│ │ ├── Color.kt
│ │ └── Type.kt
│ │
│ └── AppDrawer.kt
│
├── viewmodel
│ ├── DosenViewModel.kt
│ ├── KaprodiViewModel.kt
│ ├── LoginViewModel.kt
│ ├── MahasiswaViewModel.kt
│ ├── NilaiViewModel.kt
│ ├── JadwalViewModel.kt
│ ├── KrsViewModel.kt
│ └── NilaiViewModel.kt



## 🛠️ Teknologi

- **Jetpack Compose**
- **MVVM Architecture**
- **Material 3**
- **ViewModel & StateFlow**
- **Kotlin DSL (Gradle)**

## 📦 File Tambahan (Tidak tersedia di GitHub)

Beberapa file tidak diupload ke GitHub karena melebihi batas ukuran GitHub (100MB), silakan unduh melalui Google Drive:
 ------------------------------------------------------------------
| File               | Link Download                               |
|--------------------|---------------------------------------------|
| Demo Video (.webm) | [Klik untuk Download]([https://drive.google.com/your-demo-video-link](https://drive.google.com/file/d/1IXV6imjeOu6qipt20aNhfmMgyyqaBaln/view?usp=sharing)) |


> ⚠️ Jika link mati atau belum tersedia, silakan hubungi pembuat repo ini.

## ✍️ Kontribusi

Proyek ini dikembangkan untuk tugas rancang mata kuliah Pemrograman Mobile. Kontribusi, pull request, dan feedback sangat diterima!

## 👤 Author

- Nama: Omega Reynof Christiano
- GitHub: [@Reynof1](https://github.com/Reynof1) 

---

