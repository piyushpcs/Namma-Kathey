package com.piyush.nammakathey

data class Hero(
    val id: Int,
    val name: String,
    val district: String,
    val category: String,
    val tagline: String,
    val story: String,
    val century: Int,
    val nameKannada: String,
    val taglineKannada: String,
    val storyKannada: String,
    val memorialName: String = "",
    val memorialLat: Double = 0.0,
    val memorialLng: Double = 0.0
)