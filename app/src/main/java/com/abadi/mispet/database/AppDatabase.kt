package com.abadi.mispet.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
// --- BARIS INI YANG DIPERBAIKI ---
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.ThreadLocalRandom

// Pastikan versi database sudah 2 atau lebih tinggi
@Database(entities = [Article::class, User::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun articleDao(): ArticleDao
    abstract fun userDao(): UserDao

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
                    // Pastikan baris ini ada untuk menghindari crash saat versi berubah
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    populateDatabase(database.articleDao())
                }
            }
        }
    }
}

// Fungsi ini tidak perlu diubah, sudah benar
suspend fun populateDatabase(articleDao: ArticleDao) {
    // Hapus data lama untuk memastikan data selalu baru
    articleDao.deleteAll()

    val articlesToInsert = mutableListOf<Article>()
    val baseTitles = listOf(
        "Tips Merawat", "Panduan Budidaya", "Cara Mengatasi Penyakit pada",
        "Mengenal Karakteristik", "Rahasia Sukses Ternak", "Pupuk Terbaik untuk"
    )
    val petNames = listOf(
        "Kucing Persia", "Anjing Golden Retriever", "Ikan Cupang Hias", "Kelinci Anggora", "Burung Murai Batu"
    )
    val plantNames = listOf(
        "Pakcoy Hidroponik", "Cabai Rawit Organik", "Tanaman Tomat Cherry", "Bunga Anggrek Bulan", "Pohon Mangga Harum Manis"
    )
    val authors = listOf("Dr. Satwa", "Agro Lestari", "PetLovers ID", "Kebun Hijau", "Fauna & Flora")

    // --- PASTIKAN NAMA INI SESUAI DENGAN NAMA FILE DI DRAWABLE (TANPA .jpg/.png) ---
    val petImageNames = listOf("artikel_kucing", "artikel_anjing")
    val plantImageNames = listOf("artikel_tanaman", "artikel_pupuk")

    val random = java.util.Random()

    for (i in 1..100) {
        val isPetArticle = random.nextBoolean()

        val categoryName: String
        val imageName: String

        if (isPetArticle) {
            categoryName = petNames.random()
            imageName = petImageNames.random()
        } else {
            categoryName = plantNames.random()
            imageName = plantImageNames.random()
        }

        val baseTitle = baseTitles.random()
        val finalTitle = "$baseTitle $categoryName (Edisi #$i)"
        val author = authors.random()
        val stock = ThreadLocalRandom.current().nextInt(5, 50)
        val sold = ThreadLocalRandom.current().nextInt(100, 1000)

        val description = if (isPetArticle) {
            "Pelajari semua yang perlu Anda ketahui tentang ${categoryName.lowercase()}. Dari perawatan harian, makanan, hingga penanganan penyakit. Edisi ke-$i."
        } else {
            "Ingin sukses menanam ${categoryName.lowercase()}? Artikel ini membahas tuntas dari pemilihan bibit hingga panen. Edisi ke-$i."
        }

        val content = """
Ini adalah konten lengkap untuk artikel "$finalTitle".

**Pendahuluan**
Pembahasan kali ini berfokus pada $categoryName, sebuah topik yang banyak diminati baik oleh pemula maupun yang sudah berpengalaman. Dalam edisi ke-$i ini, kami akan mengupas tuntas berbagai aspek penting yang perlu Anda perhatikan.

**Poin-Poin Utama**
1.  **Pemilihan Bibit/Anakan:** Kunci utama keberhasilan adalah memilih bibit atau anakan yang unggul. Pastikan bebas dari penyakit dan memiliki ciri fisik yang baik.
2.  **Lingkungan Ideal:** Baik itu kandang untuk hewan atau media tanam untuk tumbuhan, lingkungan yang tepat akan memaksimalkan pertumbuhan. Suhu, kelembapan, dan pencahayaan adalah faktor krusial.
3.  **Nutrisi dan Pakan:** Kebutuhan nutrisi untuk $categoryName sangat spesifik. Artikel ini memberikan rekomendasi pakan atau pupuk terbaik yang tersedia di pasaran, dengan stok saat ini sekitar $stock unit dan telah terjual lebih dari $sold buah.
4.  **Penanganan Hama dan Penyakit:** Pencegahan lebih baik daripada mengobati. Kenali gejala-gejala awal dan cara penanganan yang efektif dan ramah lingkungan.

**Kesimpulan**
Dengan mengikuti panduan yang telah kami jabarkan, diharapkan Anda dapat meraih kesuksesan dalam merawat atau membudidayakan $categoryName. Jangan ragu untuk terus belajar dan bereksperimen.

Ditulis oleh: $author.
""".trimIndent()

        articlesToInsert.add(
            Article(
                title = finalTitle,
                description = description,
                content = content,
                author = author,
                // Pastikan imageName diisi
                imageName = imageName
            )
        )
    }

    // Masukkan semua 100 artikel ke database
    articleDao.insertAll(articlesToInsert)
}
