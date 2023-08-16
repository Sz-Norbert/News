package com.nika.news.retrofit

import com.nika.news.constants.Util.Companion.API_KEY
import com.nika.news.repository.ResponseNews
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {




    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode:String="ro",
        @Query("page")
        pageNumber : Int=1,
        @Query("apiKey")
        apiKey:String=API_KEY
    ) :Response<ResponseNews>


    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchedForNews:String="ro",
        @Query("page")
        pageNumber : Int=1,
        @Query("apiKey")
        apiKey:String=API_KEY
    ) :Response<ResponseNews>
}