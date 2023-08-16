package com.nika.news.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nika.news.R
import com.nika.news.db.ArticleDataBase
import com.nika.news.mvvm.NewsRepository
import com.nika.news.mvvm.NewsViewModel
import com.nika.news.mvvm.NewsViewModelFactory

class NewsActivity : AppCompatActivity() {

   lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)
        val articleDataBase=ArticleDataBase.getInstance(this)
        val newsRepository=NewsRepository(articleDataBase)
        val viewModelFactory=NewsViewModelFactory(newsRepository)
        viewModel=ViewModelProvider(this, viewModelFactory)[NewsViewModel::class.java]

        Log.d("//////// ", "onCreate: vm $viewModel ")

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setupWithNavController(findNavController(R.id.host_fragment))
    }
}
