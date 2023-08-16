package com.nika.news.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.nika.news.databinding.ItemArticlePreviewBinding

import com.nika.news.repository.Article

class BaseNewsAdapter:RecyclerView.Adapter<BaseNewsAdapter.ArticleViewHolder>(){

    var onItemClick:((Article) -> Unit)?=null


    inner class ArticleViewHolder(val binding: ItemArticlePreviewBinding) : ViewHolder(binding.root)


     private val differCAllBack=object : DiffUtil.ItemCallback<Article>(){
         override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
           return  oldItem.url==newItem.url
         }

         override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {

             return oldItem==newItem
         }

     }

    val differ =AsyncListDiffer(this , differCAllBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val binding = ItemArticlePreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding)
    }

     override fun getItemCount(): Int {

       return  differ.currentList.size

     }

     override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
         val article=differ.currentList[position]
         val binding= holder.binding
         holder.itemView.apply {
             Glide.with(this)
                 .load(article.urlToImage)
                 .into(binding.ivArticleImage)
             binding.tvSource.text=article.source?.name
             binding.tvTitle.text=article.title
             binding.tvDescription.text=article.description
             binding.tvPublishedAt.text=article.publishedAt
             holder.itemView.setOnClickListener{
                 onItemClick?.invoke(article)
             }
         }
     }
 }

