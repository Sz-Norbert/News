package com.androiddevs.mvvmnewsapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.nika.news.R
import com.nika.news.adapters.BaseNewsAdapter
import com.nika.news.constants.Util
import com.nika.news.databinding.FragmentBreakingNewsBinding
import com.nika.news.databinding.FragmentSavedNewsBinding
import com.nika.news.db.ArticleDataBase
import com.nika.news.mvvm.NewsRepository
import com.nika.news.mvvm.NewsViewModel
import com.nika.news.mvvm.NewsViewModelFactory
import com.nika.news.ui.NewsActivity

class SavedNewsFragment: Fragment(R.layout.fragment_saved_news) {

//    private val  viewModel:NewsViewModel by activityViewModels{
//        NewsViewModelFactory(NewsRepository())
//    }

    lateinit var binding : FragmentSavedNewsBinding
    lateinit var newsAdapter : BaseNewsAdapter
    lateinit var viewModel : NewsViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val articleDataBase= ArticleDataBase.getInstance(requireContext())
        val newsRepository=NewsRepository(articleDataBase)
        val viewModelFactory=NewsViewModelFactory(newsRepository)
        viewModel= ViewModelProvider(this, viewModelFactory)[NewsViewModel::class.java]

        setUpRecyclerView()



        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer {
            newsAdapter.differ.submitList(it)
        })

        val itemTouchHelperCallback=object :ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position=viewHolder.adapterPosition
                val article=newsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)
                Snackbar.make(view,"Successfully deleted article", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo"){
                        viewModel.saveArticle(article)
                    }
                    show()
                }


            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvSavedNews)
        }

        onItemClick()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding= FragmentSavedNewsBinding.inflate(inflater)


        return binding.root
    }

    private fun setUpRecyclerView(){
        newsAdapter= BaseNewsAdapter()
        binding.rvSavedNews.apply {
            adapter=newsAdapter
            layoutManager= LinearLayoutManager(activity)
        }
    }

    private fun onItemClick(){
        newsAdapter.onItemClick={
            val bundle=Bundle().apply {
                val json= Gson().toJson(it)
                putString(Util.JSON_KEY, json)
            }

            val articleFragment= ArticleFragment()
            articleFragment.arguments=bundle
            findNavController().navigate(R.id.action_savedNewsFragment_to_articleFragment , bundle)
        }

    }
}


