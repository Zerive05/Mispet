package com.abadi.mispet.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["phoneNumber"], unique = true)] // Memastikan nomor telepon unik
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val phoneNumber: String,
    val password: String // Di aplikasi nyata, password harus di-hash. Untuk saat ini kita simpan sebagai teks biasa.
)
