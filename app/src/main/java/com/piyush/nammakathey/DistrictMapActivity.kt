package com.piyush.nammakathey

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class DistrictMapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_district_map)

        val allHeroes = loadHeroes()

        // Group heroes by district and count
        val districtMap = allHeroes.groupBy { it.district }

        // District info with emoji and color
        val districtInfo = mapOf(
            "Dharwad" to Pair("⚔️", "#1F4E79"),
            "Belagavi" to Pair("👑", "#2E7D32"),
            "Shivamogga" to Pair("📜", "#4A148C"),
            "Chikkamagaluru" to Pair("🌿", "#1B5E20"),
            "Bagalkot" to Pair("🕊️", "#BF360C"),
            "Bengaluru" to Pair("🏙️", "#004D40"),
            "Chitradurga" to Pair("🏰", "#827717"),
            "Mysuru" to Pair("🐯", "#E65100"),
            "Kalaburagi" to Pair("✊", "#880E4F"),
            "Dakshina Kannada" to Pair("🌊", "#006064"),
            "Hassan" to Pair("🏛️", "#1A237E"),
            "Shivamogga" to Pair("🌸", "#4A148C")
        )

        val districts = districtMap.map { (districtName, heroes) ->
            val info = districtInfo[districtName] ?: Pair("🏛️", "#1F4E79")
            District(
                name = districtName,
                emoji = info.first,
                heroCount = heroes.size,
                color = info.second
            )
        }.sortedBy { it.name }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerDistricts)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = DistrictAdapter(districts)
    }

    private fun loadHeroes(): List<Hero> {
        val inputStream = assets.open("heroes.json")
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<Hero>>() {}.type
        return Gson().fromJson(reader, type)
    }
}