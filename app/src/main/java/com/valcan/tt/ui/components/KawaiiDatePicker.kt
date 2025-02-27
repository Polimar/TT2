package com.valcan.tt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.*
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch

@Composable
fun KawaiiDatePicker(
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedYear by remember { mutableStateOf(2000) }
    var selectedMonth by remember { mutableStateOf(1) }
    var selectedDay by remember { mutableStateOf(1) }
    
    val years = (1900..2024).toList()
    val months = (1..12).toList()
    val days = (1..31).toList()
    
    val yearState = rememberLazyListState(years.indexOf(selectedYear))
    val monthState = rememberLazyListState(selectedMonth - 1)
    val dayState = rememberLazyListState(selectedDay - 1)
    
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        title = { 
            Text(
                "Seleziona la tua data di nascita",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Anno
                    KawaiiNumberPicker(
                        items = years,
                        state = yearState,
                        onValueChange = { selectedYear = it },
                        label = "Anno",
                        modifier = Modifier.weight(1.2f)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Mese
                    KawaiiNumberPicker(
                        items = months,
                        state = monthState,
                        onValueChange = { selectedMonth = it },
                        label = "Mese",
                        modifier = Modifier.weight(0.9f)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Giorno
                    KawaiiNumberPicker(
                        items = days,
                        state = dayState,
                        onValueChange = { selectedDay = it },
                        label = "Giorno",
                        modifier = Modifier.weight(0.9f)
                    )
                }
            }
        },
        confirmButton = {
            KawaiiButton(
                onClick = {
                    val calendar = Calendar.getInstance()
                    calendar.set(selectedYear, selectedMonth - 1, selectedDay)
                    onDateSelected(calendar.time)
                }
            ) {
                Text("Conferma")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annulla")
            }
        }
    )
}

@Composable
private fun KawaiiNumberPicker(
    items: List<Int>,
    state: LazyListState,
    onValueChange: (Int) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
        
        Card(
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Indicatore di selezione
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                )
                
                LazyColumn(
                    state = state,
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    userScrollEnabled = true
                ) {
                    items(items) { item ->
                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                color = if (state.firstVisibleItemIndex == items.indexOf(item)) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
    
    LaunchedEffect(state.firstVisibleItemIndex) {
        onValueChange(items[state.firstVisibleItemIndex])
    }
} 