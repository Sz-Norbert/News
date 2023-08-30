package com.androiddevs.mvvmnewsapp.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.nika.news.R
import com.nika.news.adapters.BaseNewsAdapter
import com.nika.news.constants.Util
import com.nika.news.constants.Util.Companion.JSON_KEY
import com.nika.news.databinding.FragmentBreakingNewsBinding
import com.nika.news.databinding.FragmentSearchNewsBinding
import com.nika.news.db.ArticleDataBase
import com.nika.news.mvvm.NewsRepository
import com.nika.news.mvvm.NewsViewModel
import com.nika.news.mvvm.NewsViewModelFactory
import com.nika.news.mvvm.Resource
import com.nika.news.ui.NewsActivity
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchedNewsFragment: Fragment(R.layout.fragment_search_news) {

    val TAG="SearchNewsFragment"
    lateinit var newsAdapter: BaseNewsAdapter
    lateinit var binding:FragmentSearchNewsBinding
    lateinit var viewModel: NewsViewModel


    private var searchJob: Job? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val x= "szia norbi"
        val articleDataBase=ArticleDataBase.getInstance(requireContext())
        val newsRepository=NewsRepository(articleDataBase)
        val viewModelFactory=NewsViewModelFactory(newsRepository)
        viewModel= ViewModelProvider(this, viewModelFactory)[NewsViewModel::class.java]

        setUpRecyclerView()

        binding.etSearch.addTextChangedListener { searchedQuery ->
            searchJob?.cancel()
            searchJob = MainScope().launch {
                delay(500L)
                val searchQuery = searchedQuery.toString()
                if (searchQuery.isNotEmpty()) {
                    viewModel.searchNews(searchQuery)
                }
            }
        }
        onItemClick()


                viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->

                    when (response) {
                        is Resource.Success -> {
                            response.data?.let { newsresponse ->
                                hideProgressBar()
                                newsAdapter.differ.submitList(newsresponse.articles)
                            }
                        }
                        is Resource.Error -> {
                            response.message?.let { message ->
                                Log.e(TAG, " An error occured: $message")
                            }
                        }
                        is Resource.Loading -> {
                            showProgressBar()
                        }


                    }

                })

            }

            override fun onCreateView(
                inflater: LayoutInflater,
                container: ViewGroup?,
                savedInstanceState: Bundle?
            ): View? {

                binding = FragmentSearchNewsBinding.inflate(inflater)


                return binding.root
            }

            private fun setUpRecyclerView() {
                newsAdapter = BaseNewsAdapter()
                binding.rvSearchNews.apply {
                    adapter = newsAdapter
                    layoutManager = LinearLayoutManager(activity)
                }
            }

            private fun onItemClick() {
                newsAdapter.onItemClick = {
                    val bundle = Bundle().apply {
                        val json = Gson().toJson(it)
                        putString(JSON_KEY, json)
                    }

                    val articleFragment = ArticleFragment()
                    articleFragment.arguments = bundle
                    findNavController().navigate(
                        R.id.action_searchedNewsFragment_to_articleFragment,
                        bundle
                    )
                }

            }

            private fun hideProgressBar() {
                binding.paginationProgressBar.visibility = View.INVISIBLE
            }

            private fun showProgressBar() {
                binding.paginationProgressBar.visibility = View.VISIBLE
            }


}