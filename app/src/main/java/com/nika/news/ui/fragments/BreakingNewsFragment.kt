package com.nika.news.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androiddevs.mvvmnewsapp.fragments.ArticleFragment
import com.google.gson.Gson
import com.nika.news.R
import com.nika.news.adapters.BaseNewsAdapter
import com.nika.news.constants.Util.Companion.JSON_KEY
import com.nika.news.constants.Util.Companion.QUERY_PAGE_SIZE
import com.nika.news.databinding.FragmentBreakingNewsBinding
import com.nika.news.db.ArticleDataBase
import com.nika.news.mvvm.NewsRepository
import com.nika.news.mvvm.NewsViewModel
import com.nika.news.mvvm.NewsViewModelFactory
import com.nika.news.mvvm.Resource
import com.nika.news.ui.NewsActivity

class BreakingNewsFragment: Fragment(R.layout.fragment_breaking_news) {



    lateinit var viewModel : NewsViewModel
    lateinit var newsAdapter: BaseNewsAdapter
    lateinit var binding:FragmentBreakingNewsBinding
    val TAG="Breaking News Fragment"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val articleDataBase= ArticleDataBase.getInstance(requireContext())
        val newsRepository= NewsRepository(articleDataBase)
        val viewModelFactory= NewsViewModelFactory(newsRepository)
        viewModel= ViewModelProvider(this, viewModelFactory)[NewsViewModel::class.java]


        Log.d("/////", "onViewCreated: vm activit = ${(activity as NewsActivity)} ")
        setUpRecyclerView()

        newsAdapter.onItemClick
        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response->

        when(response){
            is Resource.Success ->{
                response.data?.let {newsResponse->
                    hideProgressBar()
                    newsAdapter.differ.submitList(newsResponse.articles.toList())
                    val totalPages=newsResponse.totalResults / QUERY_PAGE_SIZE+2
                    isLastPage=viewModel.breakingNewsPage==totalPages
                    if (isLastPage){
                        binding.rvBreakingNews.setPadding(0,0,0,0)
                    }
                }
            }
            is Resource.Error ->{
                response.message?.let {message->
                    Log.e(TAG, " An error occured: $message")
                }
            }
            is Resource.Loading->
            {
                showProgressBar()
            }


        }

        })



        onItemClick()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentBreakingNewsBinding.inflate(inflater)


        return binding.root
    }

    var  isLoading=false
    var isLastPage=false
    var isScrolling=false

    val scrollListener=object : RecyclerView.OnScrollListener(){


        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager=recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition= layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount=layoutManager.childCount
            val totalItemCount=layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem=firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning=firstVisibleItemPosition >=0
            val isTotalMoreThanVisible=totalItemCount >= QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning && isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                viewModel.getBreakingNews("us")
                isScrolling = false
            }

        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState==AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling=true
            }
        }
    }


    private fun setUpRecyclerView(){
        newsAdapter= BaseNewsAdapter()
        binding.rvBreakingNews.apply {
            adapter=newsAdapter
            layoutManager=LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }

    private fun onItemClick(){
        newsAdapter.onItemClick={
        val bundle=Bundle().apply {
            val json=Gson().toJson(it)
            putString(JSON_KEY, json)
            }

            val articleFragment= ArticleFragment()
            articleFragment.arguments=bundle
            findNavController().navigate(R.id.action_breakingNewsFragment_to_articleFragment, bundle)
        }

    }

    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility=View.INVISIBLE
        isLoading=false
    }
    private fun showProgressBar(){
        binding.paginationProgressBar.visibility=View.VISIBLE
        isLoading=true
    }




}