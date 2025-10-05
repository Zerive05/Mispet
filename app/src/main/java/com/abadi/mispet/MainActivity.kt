package com.abadi.mispet

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout // Pastikan import ini ada
import com.abadi.mispet.adapter.ArticleAdapter
import com.abadi.mispet.database.AppDatabase
import com.abadi.mispet.database.Article
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout // Deklarasi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi Database
        database = AppDatabase.getDatabase(this)

        // Inisialisasi RecyclerView dan Adapter
        recyclerView = findViewById(R.id.recycler_view_articles)
        articleAdapter = ArticleAdapter(emptyList<Article>()) // Mulai dengan list kosong
        recyclerView.adapter = articleAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inisialisasi SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)

        // Atur listener untuk aksi refresh
        swipeRefreshLayout.setOnRefreshListener {
            // Panggil fungsi untuk memuat ulang data
            refreshArticles()
        }

        // Ambil data dari database dan perbarui UI saat pertama kali dibuka
        observeArticles()
    }

    private fun observeArticles() {
        lifecycleScope.launch {
            database.articleDao().getAllArticles().collect { articlesFromDb ->
                Log.d("MainActivity", "Data artikel terupdate: ${articlesFromDb.size} item")
                articleAdapter.updateData(articlesFromDb)

                // === PERBAIKAN 1 (Paling Penting) ===
                // Hentikan animasi refresh SETELAH data baru berhasil ditampilkan di adapter.
                // Ini memastikan loading berhenti pada waktu yang tepat.
                if (swipeRefreshLayout.isRefreshing) {
                    swipeRefreshLayout.isRefreshing = false
                    Log.d("MainActivity", "Animasi refresh dihentikan.")
                }
            }
        }
    }

    private fun refreshArticles() {
        Log.d("MainActivity", "Memuat ulang artikel...")
        // Tidak perlu menghentikan loading di sini, karena Flow akan menanganinya.

        // Di aplikasi nyata, Anda mungkin akan mengambil data dari API di sini.
        // Untuk simulasi, kita menghapus data lama dan mengisi ulang.
        lifecycleScope.launch(Dispatchers.IO) { // Gunakan Dispatchers.IO untuk operasi database
            // Panggil fungsi static populateDatabase dari AppDatabase
            val dao = database.articleDao()

            // Hapus data lama
            dao.deleteAll()

            // Buat daftar artikel baru
            val articlesToInsert = mutableListOf<Article>()
            for (i in 1..100) {
                val article = Article(
                    title = "Artikel Baru #${System.currentTimeMillis() % 1000 + i}", // Judul unik untuk melihat perubahan
                    description = "Deskripsi untuk artikel baru nomor $i.",
                    content = "Konten lengkap untuk artikel baru #$i.",
                    author = "Penyegar"
                )
                articlesToInsert.add(article)
            }
            // Masukkan data baru
            dao.insertAll(articlesToInsert)

            // Setelah operasi database selesai, Flow di `observeArticles` akan
            // secara otomatis mendeteksi perubahan dan memicu update UI.
            // Di sanalah kita akan menghentikan animasi loading.
        }
    }
}
