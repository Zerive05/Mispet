package com.abadi.mispet

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.text.method.SingleLineTransformationMethod
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.abadi.mispet.database.AppDatabase
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var sessionManager: SessionManager
    private lateinit var database: AppDatabase
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Inisialisasi SessionManager dan Database
        sessionManager = SessionManager(this)
        database = AppDatabase.getDatabase(this)

        // 1. Temukan semua view dari layout
        val phoneEditText: EditText = findViewById(R.id.phone_edit_text)
        val passwordEditText: EditText = findViewById(R.id.password_edit_text)
        val passwordToggle: ImageView = findViewById(R.id.password_toggle)
        val loginButton: MaterialButton = findViewById(R.id.login_button)
        val registerText: TextView = findViewById(R.id.register_text)

        // 2. Tambahkan listener untuk tombol login
        loginButton.setOnClickListener {
            handleLogin(phoneEditText, passwordEditText)
        }

        // 3. Tambahkan listener untuk ikon mata (password toggle)
        passwordToggle.setOnClickListener {
            isPasswordVisible = !isPasswordVisible // Ubah status visibilitas

            if (isPasswordVisible) {
                // Jika ingin TAMPILKAN password
                passwordEditText.transformationMethod = SingleLineTransformationMethod.getInstance()
                passwordToggle.setImageResource(R.drawable.ic_eye_on) // Ganti ikon menjadi mata terbuka
            } else {
                // Jika ingin SEMBUNYIKAN password
                passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                passwordToggle.setImageResource(R.drawable.ic_eye_off) // Ganti ikon menjadi mata tertutup
            }

            // Pindahkan kursor ke akhir teks setelah mengubah visibilitas
            passwordEditText.setSelection(passwordEditText.text.length)
        }

        // 4. Tambahkan listener untuk teks registrasi
        registerText.setOnClickListener {
            // Pindah ke RegisterActivity saat teks diklik
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Fungsi untuk menangani logika validasi dan proses login dengan database.
     */
    private fun handleLogin(phoneEditText: EditText, passwordEditText: EditText) {
        val phone = phoneEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        // Validasi input
        if (phone.isEmpty()) {
            phoneEditText.error = "Nomor Handphone tidak boleh kosong"
            return // Hentikan proses
        }

        if (password.isEmpty()) {
            passwordEditText.error = "Password tidak boleh kosong"
            return // Hentikan proses
        }

        // --- PROSES LOGIN DENGAN DATABASE ---
        lifecycleScope.launch {
            // Cek ke database apakah user dengan kredensial ini ada
            val user = database.userDao().loginUser(phone, password)

            if (user != null) {
                // Jika user ditemukan (login berhasil)
                Toast.makeText(this@LoginActivity, "Login Berhasil!", Toast.LENGTH_SHORT).show()

                // Simpan status login ke session
                sessionManager.setLoggedIn(true)

                // Pindah ke MainActivity
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                // Jika user tidak ditemukan (login gagal)
                Toast.makeText(this@LoginActivity, "Nomor handphone atau password salah", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
