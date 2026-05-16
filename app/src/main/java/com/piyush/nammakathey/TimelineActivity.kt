package com.piyush.nammakathey

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class TimelineActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        val heroes = loadHeroes().sortedBy { it.century }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerTimeline)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = TimelineAdapter(heroes)
    }

    private fun loadHeroes(): List<Hero> {
        val inputStream = assets.open("heroes.json")
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<Hero>>() {}.type
        return Gson().fromJson(reader, type)
    }
}