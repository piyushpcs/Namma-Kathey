package com.piyush.nammakathey

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class QuizActivity : AppCompatActivity() {

    private lateinit var allHeroes: List<Hero>
    private lateinit var quizHeroes: List<Hero>
    private var currentIndex = 0
    private var score = 0
    private var answered = false

    private lateinit var tvQuestion: TextView
    private lateinit var tvQuestionNumber: TextView
    private lateinit var tvScore: TextView
    private lateinit var tvResult: TextView
    private lateinit var btnNext: Button
    private lateinit var resultArea: View
    private lateinit var quizProgress: android.widget.ProgressBar
    private lateinit var badgeCard: CardView
    private lateinit var tvBadgeTitle: TextView
    private lateinit var tvBadgeEmoji: TextView
    private lateinit var tvBadgeDesc: TextView
    private lateinit var options: List<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        allHeroes = loadHeroes()
        quizHeroes = allHeroes.shuffled().take(3)

        tvQuestion = findViewById(R.id.tvQuestion)
        tvQuestionNumber = findViewById(R.id.tvQuestionNumber)
        tvScore = findViewById(R.id.tvScore)
        tvResult = findViewById(R.id.tvResult)
        btnNext = findViewById(R.id.btnNext)
        resultArea = findViewById(R.id.resultArea)
        quizProgress = findViewById(R.id.quizProgress)
        badgeCard = findViewById(R.id.badgeCard)
        tvBadgeTitle = findViewById(R.id.tvBadgeTitle)
        tvBadgeEmoji = findViewById(R.id.tvBadgeEmoji)
        tvBadgeDesc = findViewById(R.id.tvBadgeDesc)

        options = listOf(
            findViewById(R.id.btnOption1),
            findViewById(R.id.btnOption2),
            findViewById(R.id.btnOption3),
            findViewById(R.id.btnOption4)
        )

        loadQuestion()

        btnNext.setOnClickListener {
            currentIndex++
            if (currentIndex < quizHeroes.size) {
                loadQuestion()
            } else {
                showFinalScore()
            }
        }

        findViewById<Button>(R.id.btnViewBadges).setOnClickListener {
            finish()
        }
    }

    private fun loadQuestion() {
        answered = false
        resultArea.visibility = View.GONE
        badgeCard.visibility = View.GONE
        options.forEach { it.visibility = View.VISIBLE }

        val hero = quizHeroes[currentIndex]
        tvQuestionNumber.text = "Question ${currentIndex + 1} of ${quizHeroes.size}"
        tvScore.text = "Score: $score | Question ${currentIndex + 1}/${quizHeroes.size}"
        quizProgress.progress = ((currentIndex + 1) * 100) / quizHeroes.size

        val questionType = currentIndex % 3
        val correctAnswer: String
        val question: String

        when (questionType) {
            0 -> {
                question = "Which district is ${hero.name} from?"
                correctAnswer = hero.district
            }
            1 -> {
                question = "What category does ${hero.name} belong to?"
                correctAnswer = hero.category
            }
            else -> {
                question = "Which hero said:\n\"${hero.tagline}\"?"
                correctAnswer = hero.name
            }
        }

        tvQuestion.text = question

        val wrongOptions = when (questionType) {
            0 -> allHeroes.filter { it.district != hero.district }
                .map { it.district }.distinct().shuffled().take(3)
            1 -> listOf("Freedom Fighter", "Poet", "Social Reformer", "Warrior King")
                .filter { it != hero.category }.shuffled().take(3)
            else -> allHeroes.filter { it.id != hero.id }
                .map { it.name }.shuffled().take(3)
        }

        val allOptions = (wrongOptions + correctAnswer).shuffled()

        options.forEachIndexed { i, btn ->
            btn.text = allOptions[i]
            btn.backgroundTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.WHITE)
            btn.setTextColor(android.graphics.Color.parseColor("#333333"))
            btn.setOnClickListener {
                if (!answered) {
                    answered = true
                    checkAnswer(btn, allOptions[i], correctAnswer)
                }
            }
        }
    }

    private fun checkAnswer(selected: Button, answer: String, correct: String) {
        if (answer == correct) {
            score++
            selected.backgroundTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#4CAF50"))
            selected.setTextColor(android.graphics.Color.WHITE)
            tvResult.text = "✅ Correct! Great job!"
            tvResult.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
        } else {
            selected.backgroundTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor("#F44336"))
            selected.setTextColor(android.graphics.Color.WHITE)
            tvResult.text = "❌ Not quite! Correct: $correct"
            tvResult.setTextColor(android.graphics.Color.parseColor("#F44336"))
            options.find { it.text == correct }?.let {
                it.backgroundTintList = android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#E8F5E9")) // Light green
                it.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
            }
        }

        tvScore.text = "Score: $score | Question ${currentIndex + 1}/${quizHeroes.size}"
        resultArea.visibility = View.VISIBLE
        btnNext.text = if (currentIndex < quizHeroes.size - 1) "Next Question →" else "See Results 🏆"
    }

    private fun showFinalScore() {
        resultArea.visibility = View.GONE
        tvQuestionNumber.visibility = View.GONE
        tvQuestion.visibility = View.GONE
        options.forEach { it.visibility = View.GONE }
        quizProgress.progress = 100

        // Determine badge
        val emoji: String
        val title: String
        val desc: String

        when (score) {
            3 -> {
                emoji = "🥇"
                title = "Gold Heritage Badge!"
                desc = "Perfect Score — Karnataka History Master!"
            }
            2 -> {
                emoji = "🥈"
                title = "Silver Heritage Badge!"
                desc = "Great job — Karnataka History Explorer!"
            }
            else -> {
                emoji = "🥉"
                title = "Bronze Heritage Badge!"
                desc = "Keep learning — Karnataka History Learner!"
            }
        }

        tvBadgeEmoji.text = emoji
        tvBadgeTitle.text = title
        tvBadgeDesc.text = desc
        badgeCard.visibility = View.VISIBLE

        // Save badge to SharedPreferences
        val prefs = getSharedPreferences("badges", MODE_PRIVATE)
        val currentBest = prefs.getInt("best_score", 0)
        if (score > currentBest) {
            prefs.edit()
                .putInt("best_score", score)
                .putString("best_badge", emoji)
                .putString("best_title", title)
                .apply()
        }
        prefs.edit().putInt("total_quizzes", prefs.getInt("total_quizzes", 0) + 1).apply()
    }

    private fun loadHeroes(): List<Hero> {
        val inputStream = assets.open("heroes.json")
        val reader = InputStreamReader(inputStream)
        val type = object : TypeToken<List<Hero>>() {}.type
        return Gson().fromJson(reader, type)
    }
}