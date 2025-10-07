package com.abadi.mispet

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.abadi.mispet.database.AppDatabase
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        database = AppDatabase.getDatabase(this)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val articleId = intent.getIntExtra("ARTICLE_ID", -1)
        if (articleId == -1) {
            Toast.makeText(this, "Artikel tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadArticleData(articleId)
        setupInteractionListeners()
    }

    private fun loadArticleData(articleId: Int) {
        lifecycleScope.launch {
            val article = database.articleDao().getArticleById(articleId)

            if (article != null) {
                val titleTextView: TextView = findViewById(R.id.article_title)
                val bodyTextView: TextView = findViewById(R.id.article_body)
                val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
                // Temukan ImageView
                val articleImageView: ImageView = findViewById(R.id.article_image)

                // Set data teks
                titleTextView.text = article.title
                bodyTextView.text = article.content
                toolbar.title = article.title

                // === LOGIKA MEMUAT GAMBAR ===
                // Dapatkan ID resource drawable dari nama string yang disimpan di database
                val imageResId = resources.getIdentifier(
                    article.imageName, // mis: "artikel_kucing"
                    "drawable",        // di dalam folder drawable
                    packageName        // di dalam package aplikasi saat ini
                )

                // Jika resource ditemukan (ID-nya bukan 0), tampilkan gambarnya.
                if (imageResId != 0) {
                    articleImageView.visibility = View.VISIBLE
                    articleImageView.setImageResource(imageResId)
                } else {
                    // Jika tidak ditemukan, sembunyikan ImageView agar tidak ada area kosong
                    articleImageView.visibility = View.GONE
                }

                val commentCountTextView: TextView = findViewById(R.id.comment_count)
                commentCountTextView.text = (10..100).random().toString()

            } else {
                Toast.makeText(this@DetailActivity, "Gagal memuat artikel", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun setupInteractionListeners() {
        val favoriteButton: MaterialButton = findViewById(R.id.favorite_button)
        val addCommentInput: TextView = findViewById(R.id.add_comment_input)

        favoriteButton.setOnClickListener {
            Toast.makeText(this, "Tombol Favorit diklik!", Toast.LENGTH_SHORT).show()
        }

        addCommentInput.setOnClickListener {
            Toast.makeText(this, "Siap untuk menambahkan komentar!", Toast.LENGTH_SHORT).show()
        }
    }
}
