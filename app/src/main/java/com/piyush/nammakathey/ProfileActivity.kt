package com.piyush.nammakathey

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val isKannada = getSharedPreferences("settings", MODE_PRIVATE)
            .getBoolean("is_kannada", false)

        loadStats()
        loadBadge()
        loadBookmarks(isKannada)
    }

    private fun loadStats() {
        val badgePrefs = getSharedPreferences("badges", MODE_PRIVATE)
        val bookmarkPrefs = getSharedPreferences("bookmarks", MODE_PRIVATE)

        val totalQuizzes = badgePrefs.getInt("total_quizzes", 0)
        val bestScore = badgePrefs.getInt("best_score", 0)

        // Count bookmarks
        val allHeroes = loadHeroes()
        val bookmarkCount = allHeroes.count { hero ->
            bookmarkPrefs.getBoolean("bookmark_${hero.id}", false)
        }

        findViewById<TextView>(R.id.tvTotalQuizzes).text = totalQuizzes.toString()
        findViewById<TextView>(R.id.tvTotalBookmarks).text = bookmarkCount.toString()
        findViewById<TextView>(R.id.tvBestScore).text = "$bestScore/3"
    }

    private fun loadBadge() {
        val prefs = getSharedPreferences("badges", MODE_PRIVATE)
        val bestScore = prefs.getInt("best_score", 0)
        val badgeEmoji = prefs.getString("best_badge", null)
        val badgeTitle = prefs.getString("best_title", null)

        if (badgeEmoji != null && badgeTitle != null) {
            findViewById<TextView>(R.id.tvProfileBadgeEmoji).text = badgeEmoji
            findViewById<TextView>(R.id.tvProfileBadgeTitle).text = badgeTitle
            findViewById<TextView>(R.id.tvProfileBadgeDesc).text =
                when (bestScore) {
                    3 -> "🌟 Perfect! You are a Karnataka History Master!"
                    2 -> "👏 Great job! You are a Karnataka History Explorer!"
                    else -> "📚 Keep learning! You are a Karnataka History Learner!"
                }
        } else {
            findViewById<TextView>(R.id.tvProfileBadgeEmoji).text = "🔒"
            findViewById<TextView>(R.id.tvProfileBadgeTitle).text = "No Badge Yet"
            findViewById<TextView>(R.id.tvProfileBadgeDesc).text =
                "Take the Hero Quiz to earn your badge!"
        }
    }

    private fun loadBookmarks(isKannada: Boolean) {
        val bookmarkPrefs = getSharedPreferences("bookmarks", MODE_PRIVATE)
        val allHeroes = loadHeroes()

        val bookmarkedHeroes = allHeroes.filter { hero ->
            bookmarkPrefs.getBoolean("bookmark_${hero.id}", false)
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerBookmarks)
        val tvNoBookmarks = findViewById<TextView>(R.id.tvNoBookmarks)

        if (bookmarkedHeroes.isEmpty()) {
            recyclerView.visibility = View.GONE
            tvNoBookmarks.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            tvNoBookmarks.visibility = View.GONE
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = HeroAdapter(bookmarkedHeroes, isKannada)
        }
    }

    private fun loadHeroes(): List<Hero> {
        val inputStream = assets.open("heroes.json")
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<Hero>>() {}.type
        return Gson().fromJson(reader, type)
    }
}