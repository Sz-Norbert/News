package com.androiddevs.mvvmnewsapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.nika.news.R
import com.nika.news.constants.Util.Companion.JSON_KEY
import com.nika.news.databinding.FragmentArticleBinding
import com.nika.news.db.ArticleDataBase
import com.nika.news.mvvm.NewsRepository
import com.nika.news.mvvm.NewsViewModel
import com.nika.news.mvvm.NewsViewModelFactory
import com.nika.news.repository.Article

class ArticleFragment : Fragment(R.layout.fragment_article)
{

    private lateinit var viewModel: NewsViewModel
    lateinit var article: Article
    private lateinit var binding: FragmentArticleBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val articleDataBase= ArticleDataBase.getInstance(requireContext())
        val newsRepository= NewsRepository(articleDataBase)
        val viewModelFactory= NewsViewModelFactory(newsRepository)
        viewModel= ViewModelProvider(this, viewModelFactory)[NewsViewModel::class.java]
        val receivedBundle=arguments
       val jsonArticle=receivedBundle?.getString(JSON_KEY)

        if (jsonArticle != null) {

             article = Gson().fromJson(jsonArticle, Article::class.java)

            binding.webView.apply {
                webViewClient= WebViewClient()
                article?.url?.let { loadUrl(it) }
            }
        }

        binding.fab.setOnClickListener {
            viewModel.saveArticle(article)
            Toast.makeText(requireContext(),"article was saved", Toast.LENGTH_SHORT).show()

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentArticleBinding.inflate(layoutInflater)
        return binding.root
    }
}
