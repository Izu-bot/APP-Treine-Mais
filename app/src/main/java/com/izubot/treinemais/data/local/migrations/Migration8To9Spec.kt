package com.izubot.treinemais.data.local.migrations

import androidx.room.DeleteColumn
import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase

@DeleteColumn(tableName = "training_history", columnName = "isUnilateral")
class Migration8To9Spec : AutoMigrationSpec {
    override fun onPostMigrate(db: SupportSQLiteDatabase) {
        // Se necessário, tratar dados após a migração
    }
}
