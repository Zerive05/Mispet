package com.abadi.mispet.database

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
    // Pastikan properti ini ada
    val imageName: String // Menyimpan nama drawable TANPA ekstensi, mis: "artikel_kucing"
)
