package com.piyush.nammakathey

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class DistrictHeroesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_district_heroes)

        val districtName = intent.getStringExtra("district_name") ?: return
        val isKannada = getSharedPreferences("settings", MODE_PRIVATE)
            .getBoolean("is_kannada", false)

        val allHeroes = loadHeroes()
        val districtHeroes = allHeroes.filter { it.district == districtName }

        findViewById<TextView>(R.id.tvDistrictTitle).text = "🏛️ $districtName"
        findViewById<TextView>(R.id.tvDistrictSubtitle).text =
            "${districtHeroes.size} hero${if (districtHeroes.size > 1) "s" else ""} from this district"

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerDistrictHeroes)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = HeroAdapter(districtHeroes, isKannada)
    }

    private fun loadHeroes(): List<Hero> {
        val inputStream = assets.open("heroes.json")
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<Hero>>() {}.type
        return Gson().fromJson(reader, type)
    }
}