package com.valcan.tt.ui.screens.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.valcan.tt.R
import com.valcan.tt.ui.navigation.Screen

@Composable
fun WelcomeScreen(
    navController: NavController,
    hasExistingUsers: Boolean? = null
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo kawaii
            Image(
                painter = painterResource(id = R.drawable.kawaii_logo),
                contentDescription = "Kawaii Logo",
                modifier = Modifier
                    .size(500.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Testo centrato su due righe
            Text(
                text = "Benvenuto in\nTrendyTracker!",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 36.dp)
            )

            // Kawaii start button
            IconButton(
                onClick = { navController.navigate(Screen.Home.route) },
                modifier = Modifier.size(80.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.kawaii_start_btn),
                    contentDescription = "Start",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
} 