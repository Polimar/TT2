package com.valcan.tt

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.valcan.tt.ui.navigation.TTNavigation
import com.valcan.tt.ui.theme.TTTheme
import com.valcan.tt.utils.LocaleHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun attachBaseContext(newBase: Context) {
        // Applica la lingua salvata o usa quella del dispositivo
        super.attachBaseContext(LocaleHelper.applyLanguage(newBase))
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TTTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    TTNavigation(navController = navController)
                }
            }
        }
    }
    
    // Override anche onConfigurationChanged per gestire i cambi di configurazione
    override fun onConfigurationChanged(newConfig: android.content.res.Configuration) {
        super.onConfigurationChanged(newConfig)
        // Riapplica la lingua salvata quando cambia la configurazione
        LocaleHelper.applyLanguage(this)
    }
}