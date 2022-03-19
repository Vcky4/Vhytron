package com.vhytron.database

import androidx.room.Room
import com.vhytron.FirebaseApp
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel {
        AppViewModel()
    }

    single {
        Room.databaseBuilder(
            androidApplication(),
            AppDatabase::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    factory {
        Repositories.RecentChatRepository()
    }

    factory {
        Repositories.UserRepository()
    }

    factory {
        Repositories.ChatRepository()
    }

    factory {
        Repositories.PeopleRepository()
    }
}