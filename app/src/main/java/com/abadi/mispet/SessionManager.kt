package com.abadi.mispet

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    // Membuat file SharedPreferences dengan nama "MispetApp"
    private var prefs: SharedPreferences = context.getSharedPreferences("MispetApp", Context.MODE_PRIVATE)

    companion object {
        // Kunci untuk menyimpan status login
        const val IS_LOGGED_IN = "isLoggedIn"
    }

    /**
     * Fungsi untuk menyimpan status login.
     * Panggil ini saat login berhasil.
     */
    fun setLoggedIn(isLoggedIn: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(IS_LOGGED_IN, isLoggedIn)
        editor.apply() // .apply() menyimpan data secara asynchronous
    }

    /**
     * Fungsi untuk mengecek status login.
     * Panggil ini untuk menentukan apakah pengguna sudah login atau belum.
     */
    fun isLoggedIn(): Boolean {
        // Mengembalikan nilai boolean. Jika tidak ditemukan, default-nya adalah 'false'.
        return prefs.getBoolean(IS_LOGGED_IN, false)
    }
}
