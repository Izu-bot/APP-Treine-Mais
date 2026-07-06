package com.izubot.treinemais.data.local.datasource

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.izubot.treinemais.data.converter.Converters
import com.izubot.treinemais.data.local.dao.ExerciseDao
import com.izubot.treinemais.data.local.dao.TrainingDao
import com.izubot.treinemais.data.local.dao.UserDao
import com.izubot.treinemais.data.local.entities.ExerciseEntity
import com.izubot.treinemais.data.local.entities.TrainingEntity
import com.izubot.treinemais.data.local.entities.User
import com.izubot.treinemais.data.local.migrations.Migration3To4Spec

@Database(
    entities = [
        User::class,
        TrainingEntity::class,
        ExerciseEntity::class
    ],
    version = 4,
    autoMigrations = [
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4, spec = Migration3To4Spec::class)
    ],
    exportSchema = true
)
@TypeConverters(Converters::class)

abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun trainingDao(): TrainingDao
    abstract fun exerciseDao(): ExerciseDao
}
