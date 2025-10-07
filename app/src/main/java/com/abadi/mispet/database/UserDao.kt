package com.abadi.mispet.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    // Fungsi untuk registrasi: Menyimpan user baru
    @Insert(onConflict = OnConflictStrategy.ABORT) // Gagal jika nomor telepon sudah ada
    suspend fun registerUser(user: User)

    // Fungsi untuk login: Mencari user berdasarkan nomor telepon dan password
    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber AND password = :password LIMIT 1")
    suspend fun loginUser(phoneNumber: String, password: String): User?

    // Fungsi untuk mengecek apakah nomor telepon sudah terdaftar (untuk validasi registrasi)
    @Query("SELECT * FROM users WHERE phoneNumber = :phoneNumber LIMIT 1")
    suspend fun findUserByPhoneNumber(phoneNumber: String): User?
}
