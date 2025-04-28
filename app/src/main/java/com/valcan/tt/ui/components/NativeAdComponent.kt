package com.valcan.tt.ui.components

import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.valcan.tt.R

@Composable
fun NativeAdComponent(adUnitId: String) {
    val context = LocalContext.current
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Pulisce l'annuncio quando il componente viene eliminato
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                nativeAd?.destroy()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            nativeAd?.destroy()
        }
    }
    
    // Carica l'annuncio nativo
    LaunchedEffect(adUnitId) {
        val adLoader = AdLoader.Builder(context, adUnitId)
            .forNativeAd { ad ->
                nativeAd?.destroy()
                nativeAd = ad
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    // Gestione dell'errore
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                    .build()
            )
            .build()
            
        adLoader.loadAd(AdRequest.Builder().build())
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        AndroidView(
            factory = { ctx ->
                // Inflater del layout dell'annuncio nativo
                val adView = LayoutInflater.from(ctx)
                    .inflate(R.layout.native_ad_layout, null) as NativeAdView
                
                // Restituisci la view per l'annuncio
                adView
            },
            modifier = Modifier.fillMaxWidth(),
            update = { adView ->
                val ad = nativeAd ?: return@AndroidView
                
                // Popola la NativeAdView con il contenuto dell'annuncio
                populateNativeAdView(ad, adView)
            }
        )
    }
}

// Funzione per popolare la NativeAdView con i dati dell'annuncio
private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
    // Riferimenti agli elementi del layout dell'annuncio
    val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
    val bodyView = adView.findViewById<TextView>(R.id.ad_body)
    val callToActionView = adView.findViewById<Button>(R.id.ad_call_to_action)
    val iconView = adView.findViewById<ImageView>(R.id.ad_app_icon)
    
    // Headline
    headlineView.text = nativeAd.headline
    adView.headlineView = headlineView
    
    // Body
    if (nativeAd.body == null) {
        bodyView.visibility = android.view.View.INVISIBLE
    } else {
        bodyView.visibility = android.view.View.VISIBLE
        bodyView.text = nativeAd.body
    }
    adView.bodyView = bodyView
    
    // Call to action
    if (nativeAd.callToAction == null) {
        callToActionView.visibility = android.view.View.INVISIBLE
    } else {
        callToActionView.visibility = android.view.View.VISIBLE
        callToActionView.text = nativeAd.callToAction
    }
    adView.callToActionView = callToActionView
    
    // Icona dell'app
    if (nativeAd.icon == null) {
        iconView.visibility = android.view.View.GONE
    } else {
        // Evita il problema di smart cast usando la variabile locale
        val adIcon = nativeAd.icon
        adIcon?.drawable?.let { drawable ->
            iconView.setImageDrawable(drawable)
            iconView.visibility = android.view.View.VISIBLE
        } ?: run {
            iconView.visibility = android.view.View.GONE
        }
    }
    adView.iconView = iconView
    
    // Registra la NativeAdView con l'annuncio nativo
    adView.setNativeAd(nativeAd)
} 