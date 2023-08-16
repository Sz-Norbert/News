package com.nika.news.repository


import com.google.gson.annotations.SerializedName

data class ResponseNews(
    @SerializedName("articles")
    val articles: MutableList<Article>,
    @SerializedName("status")
    val status: String,
    @SerializedName("totalResults")
    val totalResults: Int
)