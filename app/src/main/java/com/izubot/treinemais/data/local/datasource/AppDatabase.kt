package com.izubot.treinemais.data.local.datasource

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.izubot.treinemais.data.converter.Converters
import com.izubot.treinemais.data.local.dao.UserDao
import com.izubot.treinemais.data.local.entities.User

@Database(
    entities = [User::class], version = 2, exportSchema = true, autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
@TypeConverters(Converters::class)

abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}