package com.piyush.nammakathey

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    private lateinit var heroes: List<Hero>
    private var isKannada = false
    private lateinit var heroOfDay: Hero

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        heroes = loadHeroes()
        heroOfDay = heroes[java.util.Calendar.getInstance()
            .get(java.util.Calendar.DAY_OF_YEAR) % heroes.size]

        isKannada = getSharedPreferences("settings", MODE_PRIVATE)
            .getBoolean("is_kannada", false)

        updateUI()

        findViewById<View>(R.id.btnLangToggle).setOnClickListener {
            isKannada = !isKannada
            getSharedPreferences("settings", MODE_PRIVATE)
                .edit().putBoolean("is_kannada", isKannada).apply()
            updateUI()
        }

        findViewById<View>(R.id.btnReadStory).setOnClickListener {
            startActivity(Intent(this, HeroDetailActivity::class.java).apply {
                putExtra("hero_id", heroOfDay.id)
            })
        }

        findViewById<View>(R.id.btnExploreAll).setOnClickListener {
            startActivity(Intent(this, DistrictHeroesActivity::class.java).apply {
                putExtra("district_name", "Bengaluru")
            })
        }

        findViewById<View>(R.id.btnAllHeroes).setOnClickListener {
            startActivity(Intent(this, HeroListActivity::class.java))
        }

        findViewById<View>(R.id.btnDistrictMap).setOnClickListener {
            startActivity(Intent(this, DistrictHeroesActivity::class.java).apply {
                putExtra("district_name", "Haveri")
            })
        }

        findViewById<View>(R.id.btnQuiz).setOnClickListener {
            startActivity(Intent(this, DistrictHeroesActivity::class.java).apply {
                putExtra("district_name", "Udupi")
            })
        }

        findViewById<View>(R.id.btnTimeline).setOnClickListener {
            startActivity(Intent(this, TimelineActivity::class.java))
        }

        findViewById<View>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<View>(R.id.btnNotifications)?.setOnClickListener {
            // Optional: Show a toast or simple notification list
            android.widget.Toast.makeText(this, "No new stories today! 🔔", android.widget.Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.tvViewMap)?.setOnClickListener {
            startActivity(Intent(this, DistrictMapActivity::class.java))
        }

        findViewById<View>(R.id.btnStartQuest)?.setOnClickListener {
            startActivity(Intent(this, QuizActivity::class.java))
        }

        findViewById<EditText>(R.id.etSearch).setOnEditorActionListener { v, actionId, event ->
            val query = v.text.toString()
            if (query.isNotEmpty()) {
                startActivity(Intent(this, HeroListActivity::class.java).apply {
                    putExtra("search_query", query)
                })
                true
            } else {
                false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isKannada = getSharedPreferences("settings", MODE_PRIVATE)
            .getBoolean("is_kannada", false)
        updateUI()
    }

    private fun updateUI() {
        val toggleBtn = findViewById<TextView>(R.id.btnLangToggle)
        val nameView = findViewById<TextView>(R.id.tvHeroOfDayName)
        val districtView = findViewById<TextView>(R.id.tvHeroOfDayDistrict)
        val labelView = findViewById<TextView>(R.id.tvHeroOfDayLabel)
        val readBtn = findViewById<View>(R.id.btnReadStory)
        val tvHeroOfDayHeader = findViewById<TextView>(R.id.tvHeroOfDayHeader)
        val tvAppName = findViewById<TextView>(R.id.tvAppName)
        val tvGreeting = findViewById<TextView>(R.id.tvGreeting)
        val etSearch = findViewById<EditText>(R.id.etSearch)

        // Grid button texts
        val tvExploreAll = findViewById<TextView>(R.id.tvExploreAll)
        val tvDistrictMap = findViewById<TextView>(R.id.tvDistrictMap)
        val tvQuiz = findViewById<TextView>(R.id.tvQuiz)
        val btnTimeline = findViewById<Button>(R.id.btnTimeline)
        val btnProfile = findViewById<TextView>(R.id.btnProfile)
        val btnAllHeroes = findViewById<Button>(R.id.btnAllHeroes)

        // Dynamic counts
        val countBengaluru = heroes.count { it.district == "Bengaluru" }
        val countHaveri = heroes.count { it.district == "Haveri" }
        val countUdupi = heroes.count { it.district == "Udupi" }

        findViewById<TextView>(R.id.tvBengaluruCount).text = "$countBengaluru ${if (countBengaluru == 1) "Story" else "Stories"}"
        findViewById<TextView>(R.id.tvHaveriCount).text = "$countHaveri ${if (countHaveri == 1) "Story" else "Stories"}"
        findViewById<TextView>(R.id.tvUdupiCount).text = "$countUdupi ${if (countUdupi == 1) "Story" else "Stories"}"

        if (isKannada) {
            tvGreeting.text = "ನಮಸ್ಕಾರ! 👋"
            tvAppName.text = "ನಮ್ಮ ಕಥೆ"
            etSearch.hint = "ದಂತಕಥೆಗಳು, ಸ್ಥಳಗಳು ಅಥವಾ ವೀರರನ್ನು ಹುಡುಕಿ..."
            toggleBtn.text = "En"
            tvHeroOfDayHeader.text = "ಇಂದಿನ ಕಥೆ"
            nameView.text = heroOfDay.nameKannada
            districtView.text = heroOfDay.district
            labelView.text = "ಜಿಲ್ಲೆಗಳನ್ನು ಅನ್ವೇಷಿಸಿ"
            btnProfile.text = "ಪಿ"
            tvExploreAll.text = "ಬೆಂಗಳೂರು"
            tvDistrictMap.text = "ಹಾವೇರಿ"
            tvQuiz.text = "ಉಡುಪಿ"
            btnAllHeroes.text = "ಎಲ್ಲಾ ವೀರರನ್ನು ಅನ್ವೇಷಿಸಿ →"
            btnTimeline.text = "ಇತಿಹಾಸ ಕಾಲಸೂಚಿ 📜"
        } else {
            tvGreeting.text = "Namaskara! 👋"
            tvAppName.text = "Namma Kathey"
            etSearch.hint = "Search legends, places, or heroes..."
            toggleBtn.text = "ಕ"
            tvHeroOfDayHeader.text = "Hero of the Day"
            nameView.text = heroOfDay.name
            districtView.text = heroOfDay.district
            labelView.text = "Discover Districts"
            btnProfile.text = "JD"
            tvExploreAll.text = "Bengaluru"
            tvDistrictMap.text = "Haveri"
            tvQuiz.text = "Udupi"
            btnAllHeroes.text = "Explore All Heroes →"
            btnTimeline.text = "View History Timeline 📜"
        }
    }

    private fun loadHeroes(): List<Hero> {
        val inputStream = assets.open("heroes.json")
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<Hero>>() {}.type
        return Gson().fromJson(reader, type)
    }
}