package com.vhytron.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [UserEntity::class, ChatEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun chatDao(): ChatsDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDataBase(context: Context): AppDatabase{
            val tempInstance = INSTANCE
            if (tempInstance !=null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }

    }
}