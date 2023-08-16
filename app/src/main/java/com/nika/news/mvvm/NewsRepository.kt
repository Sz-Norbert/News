package com.nika.news.mvvm

import com.nika.news.db.ArticleDataBase
import com.nika.news.repository.Article
import com.nika.news.retrofit.RetrofitInstance

class NewsRepository(
    val db:ArticleDataBase
) {

    suspend fun getBreakingNews(countryCode: String , pageNumber:Int)=
        RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)


    suspend fun searchNews(searchedQuery:String, pageNumber: Int)=
        RetrofitInstance.api.searchForNews(searchedQuery,pageNumber)

    suspend fun upsert(article: Article)=db.articleDao().upsert(article)

    fun getSavedNews()=db.articleDao().getAllArticles()

    suspend fun deleteArticle(article: Article)=db.articleDao().deleteArticle(article)
}
