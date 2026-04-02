package com.izubot.treinemais.data.local.entities

enum class SyncStatus {
    SYNCED,     // Dado igual ao do servidor
    PENDING,    // Alterado localmente, aguardando sincronização
    ERROR,      // Falha ao tentar sincronizar
    IN_PROGRESS // Sincronização em andamento
}