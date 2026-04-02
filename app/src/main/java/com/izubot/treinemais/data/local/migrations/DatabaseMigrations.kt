package com.izubot.treinemais.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DatabaseMigrations {

    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE `user` ADD COLUMN `guid_user` TEXT NOT NULL DEFAULT ''"
            )
            db.execSQL(
                "CREATE UNIQUE INDEX IF NOT EXISTS `index_user_guid_user` ON `user` (`guid_user`)"
            )
        }
    }

    // Futuras migrações ficam aqui
    // val MIGRATION_2_3 = object : Migration(2, 3) { ... }

    val ALL = arrayOf(MIGRATION_1_2)
}