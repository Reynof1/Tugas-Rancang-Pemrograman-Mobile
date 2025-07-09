---

### 📱 SIASAT Mobile – Sistem Informasi Akademik Mahasiswa dan Dosen

Aplikasi mobile berbasis **Jetpack Compose** yang terintegrasi dengan **Firebase Realtime Database & Authentication**, dirancang untuk membantu kegiatan akademik mahasiswa, dosen, dan kaprodi dalam satu platform.

---

### 🧩 Fitur Utama

#### 👨‍🎓 Mahasiswa

* **Login Otentikasi**
* **Isi Kartu Rencana Studi (KRS)**
* **Lihat Jadwal Kuliah**
* **Kartu Studi (KHS)**
* **Transkrip Nilai & Hasil Studi per Semester**
* **Presensi (Absen Online) secara Real-time**

#### 👨‍🏫 Dosen

* **Lihat Daftar Mata Kuliah yang Diampu**
* **Buka & Tutup Sesi Presensi**
* **Input Nilai Mahasiswa**
* **Lihat Riwayat Presensi Tiap Sesi**

#### 👨‍💼 Kaprodi

* **Tambah Mata Kuliah Baru**
* **Tentukan Dosen Pengampu**
* **Manajemen Struktur Akademik**

---

### 🛠 Teknologi

* **Kotlin** (Jetpack Compose UI)
* **Firebase Realtime Database**
* **Firebase Auth**
* **MVVM Architecture**
* **Navigation Compose**
* **StateFlow & ViewModel**

---

### 🗂 Struktur Folder Utama

```
com.example.tugasrancang
├── model                 → Data model (e.g. TranskripMatkul.kt)
├── ui
│   ├── login             → LoginScreen.kt
│   ├── dosen             → Semua fitur & tampilan untuk dosen
│   ├── mahasiswa         → Semua fitur & tampilan mahasiswa
│   ├── kaprodi           → Form tambah matakuliah oleh kaprodi
│   └── theme             → Custom theme, color & typography
├── viewmodel             → ViewModel untuk setiap role (MVVM)
```

---

### 🚀 Cara Menjalankan

1. Buka proyek di **Android Studio**
2. Pastikan koneksi internet aktif (menggunakan Firebase)
3. Jalankan emulator atau device fisik
4. Login menggunakan akun Firebase yang valid

---

### 🔐 Login Role (contoh data Firebase)

| Role      | Email                                             | Password |
| --------- | ------------------------------------------------- | -------- |
| Mahasiswa | [mahasiswa@gmail.com](mailto:mahasiswa@gmail.com) | mahasiswa   |
| Dosen     | [dosen@gmail.com](mailto:dosen@gmail.com)         | dosenn   |
| Kaprodi   | [kaprodi@gmail.com](mailto:kaprodi@gmail.com)     | kaprodi   |

---

### ✨ Catatan

* Proyek ini **belum support upload video/webm besar ke GitHub** (karena limitasi file >100MB).
* Untuk menyimpan video demonstrasi, ada dalam link berikut : [https://drive.google.com/file/d/1IXV6imjeOu6qipt20aNhfmMgyyqaBaln/view?usp=sharing]

---
