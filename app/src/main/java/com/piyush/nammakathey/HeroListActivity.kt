package com.piyush.nammakathey

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class HeroListActivity : AppCompatActivity() {

    private lateinit var allHeroes: List<Hero>
    private lateinit var adapter: HeroAdapter
    private var activeCategory = "All"
    private var searchQuery = ""
    private var isKannada = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hero_list)

        allHeroes = loadHeroes()

        // Read global language preference
        isKannada = getSharedPreferences("settings", MODE_PRIVATE)
            .getBoolean("is_kannada", false)

        val initialSearch = intent.getStringExtra("search_query") ?: ""
        searchQuery = initialSearch

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewHeroes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = HeroAdapter(allHeroes, isKannada)
        recyclerView.adapter = adapter

        val etSearch = findViewById<EditText>(R.id.etSearch)
        if (initialSearch.isNotEmpty()) {
            etSearch.setText(initialSearch)
            applyFilter()
        }

        // Search
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                searchQuery = s.toString()
                applyFilter()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Filter buttons
        val btnAll = findViewById<Button>(R.id.btnFilterAll)
        val btnFreedom = findViewById<Button>(R.id.btnFilterFreedom)
        val btnPoet = findViewById<Button>(R.id.btnFilterPoet)
        val btnReformer = findViewById<Button>(R.id.btnFilterReformer)

        btnAll.setOnClickListener { setFilter("All", btnAll, btnFreedom, btnPoet, btnReformer) }
        btnFreedom.setOnClickListener { setFilter("Freedom Fighter", btnFreedom, btnAll, btnPoet, btnReformer) }
        btnPoet.setOnClickListener { setFilter("Poet", btnPoet, btnAll, btnFreedom, btnReformer) }
        btnReformer.setOnClickListener { setFilter("Social Reformer", btnReformer, btnAll, btnFreedom, btnPoet) }
    }

    private fun setFilter(category: String, active: Button, vararg inactive: Button) {
        activeCategory = category
        active.backgroundTintList = android.content.res.ColorStateList.valueOf(
            android.graphics.Color.parseColor("#1F4E79"))
        active.setTextColor(android.graphics.Color.WHITE)
        inactive.forEach {
            it.backgroundTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#E0E0E0"))
            it.setTextColor(android.graphics.Color.parseColor("#333333"))
        }
        applyFilter()
    }

    private fun applyFilter() {
        val filtered = allHeroes.filter { hero ->
            val matchesCategory = activeCategory == "All" || hero.category == activeCategory
            val matchesSearch = hero.name.contains(searchQuery, ignoreCase = true) ||
                    hero.district.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
        adapter.updateList(filtered)
    }

    private fun loadHeroes(): List<Hero> {
        val inputStream = assets.open("heroes.json")
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<Hero>>() {}.type
        return Gson().fromJson(reader, type)
    }
}