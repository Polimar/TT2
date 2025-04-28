package com.valcan.tt

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TTApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Inizializza l'SDK Google Mobile Ads
        MobileAds.initialize(this) {}
        
        // Configurazione per la modalità test (da rimuovere in produzione)
        val testDeviceIds = listOf("ABCDEF012345") // Sostituire con i device ID di test
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(testDeviceIds)
            .build()
        MobileAds.setRequestConfiguration(configuration)
    }
} 