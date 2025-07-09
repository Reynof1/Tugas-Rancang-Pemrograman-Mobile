package com.example.tugasrancang.model

data class TranskripMatkul(
    val kodeMatkul: String = "",
    val namaMatkul: String = "",
    val sks: Int = 0,
    val nilai: Int = 0,
    val grade: String = "",
    val semester: String = ""
)

data class HasilStudiSemester(
    val semester: String = "",
    val jumlahSks: Int = 0,
    val ip: Double = 0.0,
    val ipk: Double = 0.0,
    val detail: List<TranskripMatkul> = emptyList()
)
