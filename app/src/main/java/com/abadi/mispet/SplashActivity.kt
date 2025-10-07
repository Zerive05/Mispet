package com.abadi.mispet

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Handle the splash screen transition.
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash) // Tetap gunakan layout splash

        // Delay 2 detik sebelum pindah ke activity selanjutnya
        Handler(Looper.getMainLooper()).postDelayed({

            // HAPUS SEMUA LOGIKA IF-ELSE LOGIN DI SINI

            // Langsung arahkan ke WelcomeActivity
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish() // Tutup SplashActivity agar tidak bisa kembali ke sini

        }, 2000) // 2000 milidetik = 2 detik
    }
}
