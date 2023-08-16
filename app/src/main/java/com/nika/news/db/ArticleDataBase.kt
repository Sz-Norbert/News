package com.nika.news.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nika.news.repository.Article

@Database(
  entities = [Article::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract  class ArticleDataBase:RoomDatabase(){

  abstract fun articleDao():ArticleDao


  companion object  {
    @Volatile
    var INSTANCE : ArticleDataBase?=null

    @Synchronized
    fun getInstance(context: Context):ArticleDataBase{
      if (INSTANCE==null){
        INSTANCE=Room.databaseBuilder(
          context,
          ArticleDataBase::class.java,
          "article.db"
        ).fallbackToDestructiveMigration()
          .build()
      }
      return INSTANCE as ArticleDataBase
    }
  }
}