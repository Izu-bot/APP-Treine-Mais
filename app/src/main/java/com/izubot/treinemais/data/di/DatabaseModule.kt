package com.izubot.treinemais.data.di

import android.content.Context
import androidx.room.Room
import com.izubot.treinemais.data.local.dao.UserDao
import com.izubot.treinemais.data.local.datasource.AppDatabase
import com.izubot.treinemais.data.local.migrations.DatabaseMigrations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "treine_mais_db"
        )
            .addMigrations(*DatabaseMigrations.ALL)
            .build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao { return database.userDao() }

}