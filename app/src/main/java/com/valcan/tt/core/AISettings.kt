package com.valcan.tt.core

object AISettings {
    // Configurazioni Database
    const val DATABASE_NAME = "trendy_tracker_db"
    const val DATABASE_VERSION = 1

    // Configurazioni Immagini
    const val MAX_IMAGE_SIZE = 1024 * 1024 // 1MB
    const val IMAGE_QUALITY = 80
    const val IMAGE_FORMAT = "jpg"
    
    // Configurazioni Cache
    const val CACHE_SIZE = 50 * 1024 * 1024 // 50MB
    const val CACHE_VALIDITY_DAYS = 7
    
    // Configurazioni Backup
    const val BACKUP_FOLDER = "TrendyTracker/backups"
    const val BACKUP_FORMAT = "tt_backup"
    
    // Configurazioni UI
    object UI {
        const val MIN_SEARCH_LENGTH = 3
        const val GRID_COLUMNS = 2
        const val LIST_PAGE_SIZE = 20
        
        // Animazioni
        const val ANIMATION_DURATION = 300
        const val TRANSITION_DURATION = 500
    }
    
    // Configurazioni Validazione
    object Validation {
        const val MIN_NAME_LENGTH = 2
        const val MAX_NAME_LENGTH = 50
        const val MIN_DESCRIPTION_LENGTH = 10
        const val MAX_DESCRIPTION_LENGTH = 500
        const val MIN_PRICE = 0.0
        const val MAX_PRICE = 99999.99
    }
    
    // Configurazioni Sincronizzazione
    object Sync {
        const val SYNC_INTERVAL = 24 * 60 * 60 * 1000 // 24 ore
        const val MAX_RETRY_ATTEMPTS = 3
        const val RETRY_DELAY = 5000L // 5 secondi
    }
} 