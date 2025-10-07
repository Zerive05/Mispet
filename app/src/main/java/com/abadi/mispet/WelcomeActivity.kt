package com.abadi.mispet

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Inisialisasi SessionManager untuk memeriksa status login
        sessionManager = SessionManager(this)

        // Temukan tombol "Lanjut" dari layout
        val continueButton: LinearLayout = findViewById(R.id.continue_button)

        // Tambahkan listener untuk tombol "Lanjut"
        continueButton.setOnClickListener {
            // Cek status login saat tombol ditekan
            checkLoginStatus()
        }
    }

    /**
     * Fungsi yang memeriksa status login dan mengarahkan ke activity yang sesuai.
     * Ini adalah logika yang kita pindahkan dari SplashActivity.
     */
    private fun checkLoginStatus() {
        if (sessionManager.isLoggedIn()) {
            // Jika pengguna sudah login, langsung ke MainActivity
            navigateTo(MainActivity::class.java)
        } else {
            // Jika belum login, arahkan ke LoginActivity
            navigateTo(LoginActivity::class.java)
        }
    }

    /**
     * Fungsi bantuan untuk navigasi dan membersihkan activity sebelumnya.
     */
    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        // Flag ini penting agar pengguna tidak bisa kembali ke WelcomeActivity dengan tombol back
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}
