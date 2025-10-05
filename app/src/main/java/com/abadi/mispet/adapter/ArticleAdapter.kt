package com.abadi.mispet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.abadi.mispet.R
import com.abadi.mispet.database.Article

class ArticleAdapter(private var articles: List<Article>) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    // Kelas ini memegang referensi ke view di dalam item_article.xml
    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.article_title_text)
        val descriptionTextView: TextView = itemView.findViewById(R.id.article_description_text)
        val imageView: ImageView = itemView.findViewById(R.id.article_image)
    }

    // Membuat ViewHolder baru (dipanggil oleh layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    // Mengikat data dari list 'articles' ke view di dalam ViewHolder
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.titleTextView.text = article.title
        holder.descriptionTextView.text = article.description
        // Di sini Anda bisa menambahkan logika untuk memuat gambar dari article.imageUrl jika ada
        // Contoh: Glide.with(holder.itemView.context).load(article.imageUrl).into(holder.imageView)
    }

    // Mengembalikan jumlah total item dalam list
    override fun getItemCount(): Int {
        return articles.size
    }

    // Fungsi untuk memperbarui data di adapter
    fun updateData(newArticles: List<Article>) {
        articles = newArticles
        notifyDataSetChanged() // Memberi tahu RecyclerView untuk me-render ulang
    }
}
