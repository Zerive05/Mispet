package com.abadi.mispet.database // Sesuaikan dengan nama paket Anda

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,
    val description: String,
    val content: String,
    val author: String,
    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null
// Tambahkan properti lain sesuai kebutuhan
)
    