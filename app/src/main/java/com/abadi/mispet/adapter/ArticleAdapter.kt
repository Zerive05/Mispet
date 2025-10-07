package com.abadi.mispet.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abadi.mispet.DetailActivity
import com.abadi.mispet.R
import com.abadi.mispet.database.Article

class ArticleAdapter(private var articles: List<Article>) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    // ViewHolder akan menampung referensi ke view di dalam setiap item
    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // --- PERBAIKI REFERENSI ID DI SINI ---
        // Sesuaikan dengan ID yang ada di item_article.xml
        val titleTextView: TextView = itemView.findViewById(R.id.article_item_title)
        val descriptionTextView: TextView = itemView.findViewById(R.id.article_item_description)
        val thumbnailImageView: ImageView = itemView.findViewById(R.id.article_thumbnail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        // Membuat view baru dari layout item_article.xml
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun getItemCount(): Int = articles.size

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        // Mendapatkan artikel pada posisi saat ini
        val article = articles[position]

        // Mengisi data artikel ke dalam view yang ada di ViewHolder
        holder.titleTextView.text = article.title
        holder.descriptionTextView.text = article.description

        // Mengatur gambar thumbnail
        val context = holder.itemView.context
        val imageResId = context.resources.getIdentifier(
            article.imageName,
            "drawable",
            context.packageName
        )

        if (imageResId != 0) {
            holder.thumbnailImageView.setImageResource(imageResId)
        } else {
            // Jika gambar tidak ditemukan, set gambar placeholder atau sembunyikan
            holder.thumbnailImageView.setImageResource(R.drawable.ic_menu) // Ganti dengan drawable placeholder Anda
        }

        // Menambahkan OnClickListener untuk setiap item
        holder.itemView.setOnClickListener {
            val intent = Intent(context, DetailActivity::class.java).apply {
                putExtra("ARTICLE_ID", article.id)
            }
            context.startActivity(intent)
        }
    }

    // Fungsi untuk memperbarui data di adapter
    fun updateData(newArticles: List<Article>) {
        this.articles = newArticles
        notifyDataSetChanged() // Memberi tahu RecyclerView untuk me-render ulang
    }
}
