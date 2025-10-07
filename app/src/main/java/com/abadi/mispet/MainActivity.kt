package com.abadi.mispet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.PopupMenu // <-- Import PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.abadi.mispet.adapter.ArticleAdapter
import com.abadi.mispet.database.AppDatabase
import com.abadi.mispet.database.populateDatabase
// import com.google.android.material.button.MaterialButton (Sudah tidak perlu)
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var searchView: SearchView
    private lateinit var sessionManager: SessionManager

    private val searchQuery = MutableStateFlow("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sessionManager = SessionManager(this)

        database = AppDatabase.getDatabase(this)
        recyclerView = findViewById(R.id.recycler_view_articles)
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)
        searchView = findViewById(R.id.search_view)

        setupRecyclerView()
        setupSearchView()
        setupSwipeRefresh()
        observeArticles()

        // --- LOGIKA MENU BARU ---
        // 1. Temukan tombol menu dari navbar
        val menuButton: ImageView = findViewById(R.id.menu_button)
        // 2. Tambahkan listener untuk menampilkan popup menu
        menuButton.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    /**
     * Fungsi untuk menampilkan PopupMenu saat ikon menu diklik.
     */
    private fun showPopupMenu(anchorView: View) {
        val popup = PopupMenu(this, anchorView)
        popup.menuInflater.inflate(R.menu.main_menu, popup.menu)

        // Listener untuk item di dalam menu
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    handleLogout() // Panggil fungsi logout yang sudah ada
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    /**
     * Fungsi untuk menangani proses logout.
     * (Fungsi ini tidak berubah, hanya dipanggil dari tempat yang berbeda)
     */
    private fun handleLogout() {
        sessionManager.setLoggedIn(false)
        Toast.makeText(this, "Anda telah logout", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter(emptyList())
        recyclerView.adapter = articleAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            refreshArticles()
        }
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchQuery.value = newText.orEmpty()
                return true
            }
        })
    }

    private fun observeArticles() {
        lifecycleScope.launch {
            searchQuery
                .debounce(300)
                .flatMapLatest { query ->
                    val formattedQuery = "%$query%"
                    database.articleDao().getAllArticles(formattedQuery)
                }
                .collect { articlesFromDb ->
                    Log.d("MainActivity", "Data terupdate untuk query '${searchQuery.value}': ${articlesFromDb.size} item")
                    articleAdapter.updateData(articlesFromDb)

                    if (swipeRefreshLayout.isRefreshing) {
                        swipeRefreshLayout.isRefreshing = false
                        Log.d("MainActivity", "Animasi refresh dihentikan.")
                    }
                }
        }
    }

    private fun refreshArticles() {
        searchView.setQuery("", false)
        searchView.clearFocus()
        searchQuery.value = ""

        Log.d("MainActivity", "Memuat ulang artikel...")
        lifecycleScope.launch(Dispatchers.IO) {
            populateDatabase(database.articleDao())
        }
    }
}
