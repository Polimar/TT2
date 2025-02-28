package com.valcan.tt.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.valcan.tt.R
import com.valcan.tt.data.model.User
import com.valcan.tt.ui.components.TTBottomNavigation
import com.valcan.tt.ui.screens.welcome.UserSelectionDialog
import com.valcan.tt.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    var showUserSelectionDialog by remember { mutableStateOf(false) }
    val users by viewModel.users.collectAsState(initial = emptyList())

    Scaffold(
        bottomBar = { TTBottomNavigation(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Titolo
            Text(
                text = "Impostazioni",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            // Griglia di impostazioni
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(6) { index ->
                    when (index) {
                        0 -> SettingButton(
                            icon = R.drawable.ic_users_kawaii,
                            contentDescription = "Selezione Utenti",
                            onClick = { showUserSelectionDialog = true }
                        )
                        1 -> SettingButton(
                            icon = R.drawable.ic_wardrobe_kawaii,
                            contentDescription = "Armadi",
                            onClick = { /* TODO */ }
                        )
                        2 -> SettingButton(
                            icon = R.drawable.ic_add_male_kawaii,
                            contentDescription = "Nuovo Utente M",
                            onClick = { /* TODO */ }
                        )
                        3 -> SettingButton(
                            icon = R.drawable.ic_add_female_kawaii,
                            contentDescription = "Nuovo Utente F",
                            onClick = { /* TODO */ }
                        )
                        4 -> SettingButton(
                            icon = R.drawable.ic_backup_kawaii,
                            contentDescription = "Backup",
                            onClick = { /* TODO */ }
                        )
                        5 -> SettingButton(
                            icon = R.drawable.ic_info_kawaii,
                            contentDescription = "Info App",
                            onClick = { /* TODO */ }
                        )
                    }
                }
            }
        }
    }

    if (showUserSelectionDialog) {
        UserSelectionDialog(
            users = users,
            onUserSelected = { selectedUser ->
                viewModel.updateCurrentUser(selectedUser)
                showUserSelectionDialog = false
            }
        )
    }
}

@Composable
fun SettingButton(
    icon: Int,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = contentDescription,
            modifier = Modifier.size(120.dp)
        )
    }
} 