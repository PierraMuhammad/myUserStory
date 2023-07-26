package com.dicoding.picodiploma.mystoryapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dicoding.picodiploma.mystoryapp.data.response.ListStoryItem

@Database(
    entities = [ListStoryItem::class, RemoteKey::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase: RoomDatabase() {
    abstract fun storyDao() : StoryDao
    abstract fun remoteKeyDao() : RemoteKeyDao

    companion object{
        @Volatile
        private var Instance: StoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): StoryDatabase{
            return Instance ?: synchronized(this){
                Instance ?: Room.databaseBuilder(context.applicationContext, StoryDatabase::class.java, "database_story")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also {
                        Instance = it
                    }
            }
        }
    }
}
