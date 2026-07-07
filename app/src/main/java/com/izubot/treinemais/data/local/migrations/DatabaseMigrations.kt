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

    /**
     * GUIA DE ATUALIZAÇÃO DO BANCO DE DADOS:
     *
     * 1. Modifique as entidades (@Entity).
     * 2. Incremente a 'version' em AppDatabase.kt.
     * 3. Se a mudança for simples (adicionar coluna, nova tabela), use 'autoMigrations' em AppDatabase.
     * 4. Se a mudança for complexa (renomear coluna, deletar tabela, mudar tipos),
     *    implemente uma Migration manual aqui e adicione ao array 'ALL'.
     * 5. SEMPRE verifique se os arquivos .json na pasta 'schemas' foram gerados após build.
     */
    val ALL = arrayOf(MIGRATION_1_2)
}

