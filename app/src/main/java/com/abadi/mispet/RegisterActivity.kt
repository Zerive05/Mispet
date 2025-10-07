package com.abadi.mispet

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.abadi.mispet.database.AppDatabase
import com.abadi.mispet.database.User
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        database = AppDatabase.getDatabase(this)

        val phoneEditText = findViewById<EditText>(R.id.phone_edit_text_register)
        val passwordEditText = findViewById<EditText>(R.id.password_edit_text_register)
        val confirmPasswordEditText = findViewById<EditText>(R.id.confirm_password_edit_text_register)
        val registerButton = findViewById<Button>(R.id.register_button)
        val loginText = findViewById<TextView>(R.id.login_text_from_register)

        registerButton.setOnClickListener {
            val phone = phoneEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            // Validasi Input
            if (phone.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Semua kolom harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                Toast.makeText(this, "Password tidak cocok", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Proses Registrasi ke Database
            lifecycleScope.launch {
                try {
                    // Cek dulu apakah user sudah ada
                    val existingUser = database.userDao().findUserByPhoneNumber(phone)
                    if (existingUser != null) {
                        Toast.makeText(this@RegisterActivity, "Nomor handphone sudah terdaftar", Toast.LENGTH_SHORT).show()
                    } else {
                        // Jika belum ada, daftarkan user baru
                        val newUser = User(phoneNumber = phone, password = password)
                        database.userDao().registerUser(newUser)
                        Toast.makeText(this@RegisterActivity, "Registrasi berhasil! Silakan login.", Toast.LENGTH_LONG).show()
                        finish() // Kembali ke halaman login setelah berhasil
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@RegisterActivity, "Registrasi gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        loginText.setOnClickListener {
            finish() // Kembali ke halaman login
        }
    }
}
