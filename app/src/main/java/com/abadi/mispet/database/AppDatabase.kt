package com.abadi.mispet.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Article::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao

    // Companion object HARUS berada di level ini, langsung di dalam AppDatabase
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mispet_database"
                )
                    .addCallback(AppDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    // HANYA SATU KELAS CALLBACK YANG BENAR
    private class AppDatabaseCallback : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    // Sekarang fungsi ini bisa dipanggil karena berada dalam scope yang sama
                    populateDatabase(database.articleDao())
                }
            }
        }

        // Fungsi populateDatabase berada di sini, sebagai anggota dari AppDatabaseCallback
        suspend fun populateDatabase(articleDao: ArticleDao) {
            // Hapus data lama untuk memastikan data selalu baru saat testing
            articleDao.deleteAll()

            // Buat daftar artikel baru untuk ditambahkan
            val articlesToInsert = mutableListOf<Article>()

            // Lakukan perulangan sebanyak 100 kali
            for (i in 1..100) {
                val article = Article(
                    // id akan dibuat otomatis, jadi tidak perlu disetel
                    title = "Judul Artikel #$i",
                    description = "Ini adalah deskripsi singkat untuk artikel nomor $i. Konten ini dibuat secara otomatis untuk tujuan pengujian.",
                    content = "Ini adalah isi konten lengkap dari artikel #$i. Di aplikasi nyata, konten ini akan jauh lebih panjang dan mendetail, membahas topik yang relevan dengan judul.",
                    author = if (i % 2 == 0) "Tim Mispet" else "Ahli Pertanian" // Contoh penulis yang bervariasi
                )
                articlesToInsert.add(article)
            }

            // Masukkan semua artikel ke database dalam satu kali operasi (lebih efisien)
            articleDao.insertAll(articlesToInsert)
        }
    }
}
