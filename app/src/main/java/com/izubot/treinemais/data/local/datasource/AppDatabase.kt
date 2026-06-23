package com.izubot.treinemais.data.local.datasource

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.izubot.treinemais.data.converter.Converters
import com.izubot.treinemais.data.local.dao.ExerciseDao
import com.izubot.treinemais.data.local.dao.UserDao
import com.izubot.treinemais.data.local.entities.ExerciseEntity
import com.izubot.treinemais.data.local.entities.User

@Database(
    entities = [
        User::class,
        ExerciseEntity::class],
    version = 3,
    exportSchema = true
)
@TypeConverters(Converters::class)

abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun exerciseDao(): ExerciseDao
}