package com.valcan.tt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KawaiiDatePicker(
    //initialDate: Date = Date(),
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit
) {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    
    // Inizializzo con valori di default
    var selectedDay by remember { mutableStateOf(15) }
    var selectedMonth by remember { mutableStateOf(6) }
    var selectedYear by remember { mutableStateOf(2000) }
    
    // Uso il metodo più semplice suggerito
    val years = (1936..currentYear).toList()
    val months = (1..12).toList() + (1..12).toList() + (1..12).toList()
    val days = (1..31).toList() + (1..31).toList() + (1..31).toList()
    
    // Posizioni iniziali (al centro delle liste)
    val dayState = rememberLazyListState(initialFirstVisibleItemIndex = 31)
    val monthState = rememberLazyListState(initialFirstVisibleItemIndex = 12)
    val yearState = rememberLazyListState(initialFirstVisibleItemIndex = (years.size - 1) / 2)
    
    // Traccia i valori correntemente selezionati
    LaunchedEffect(dayState.firstVisibleItemIndex) {
        val index = dayState.firstVisibleItemIndex + 1 // +1 perché vogliamo il secondo elemento visibile (centrale)
        if (index < days.size) {
            selectedDay = days[index]
        }
    }
    
    LaunchedEffect(monthState.firstVisibleItemIndex) {
        val index = monthState.firstVisibleItemIndex + 1
        if (index < months.size) {
            selectedMonth = months[index]
        }
    }
    
    LaunchedEffect(yearState.firstVisibleItemIndex) {
        val index = yearState.firstVisibleItemIndex + 1
        if (index < years.size) {
            selectedYear = years[index]
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Seleziona la tua\ndata di nascita",
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Cilindro per i giorni
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = "Giorno",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(bottom = 6.dp)
                        )
                        
                        // Rettangolo centrale evidenziato
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth()
                                .height(50.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        )
                        
                        LazyColumn(
                            state = dayState,
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            contentPadding = PaddingValues(vertical = 50.dp) // Per far vedere 3 item
                        ) {
                            items(days) { day ->
                                Box(
                                    modifier = Modifier
                                        .height(50.dp)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day.toString(),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (day == selectedDay) 
                                            MaterialTheme.colorScheme.primary 
                                        else 
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                    )
                                }
                            }
                        }
                    }
                    
                    // Cilindro per i mesi
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = "Mese",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(bottom = 6.dp)
                        )
                        
                        // Rettangolo centrale evidenziato
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth()
                                .height(50.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        )
                        
                        LazyColumn(
                            state = monthState,
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            contentPadding = PaddingValues(vertical = 50.dp)
                        ) {
                            items(months) { month ->
                                Box(
                                    modifier = Modifier
                                        .height(50.dp)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = month.toString(),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (month == selectedMonth) 
                                            MaterialTheme.colorScheme.primary 
                                        else 
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                    )
                                }
                            }
                        }
                    }
                    
                    // Cilindro per gli anni
                    Box(
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxHeight()
                    ) {
                        Text(
                            text = "Anno",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(bottom = 6.dp)
                        )
                        
                        // Rettangolo centrale evidenziato
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth()
                                .height(50.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        )
                        
                        LazyColumn(
                            state = yearState,
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            contentPadding = PaddingValues(vertical = 50.dp)
                        ) {
                            items(years) { year ->
                                Box(
                                    modifier = Modifier
                                        .height(50.dp)
                                        .fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = year.toString(),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = if (year == selectedYear) 
                                            MaterialTheme.colorScheme.primary 
                                        else 
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            KawaiiButton(onClick = {
                val calendar = Calendar.getInstance()
                // Aggiunto controllo per giorni validi in base al mese
                val adjustedDay = when (selectedMonth) {
                    2 -> selectedDay.coerceAtMost(if (selectedYear % 4 == 0 && (selectedYear % 100 != 0 || selectedYear % 400 == 0)) 29 else 28)
                    4, 6, 9, 11 -> selectedDay.coerceAtMost(30)
                    else -> selectedDay
                }
                
                calendar.set(selectedYear, selectedMonth - 1, adjustedDay)
                onDateSelected(calendar.time)
            }) {
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
fun NumberSelector(
    label: String,
    values: List<Int>,
    initialValue: Int,
    onValueChange: (Int) -> Unit
) {
    var currentValue by remember { mutableStateOf(initialValue) }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        // Label
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Valore precedente (più piccolo e trasparente)
        val prevIndex = (values.indexOf(currentValue) - 1).coerceAtLeast(0)
        val prevValue = if (prevIndex >= 0) values[prevIndex] else values.last()
        Text(
            text = prevValue.toString(),
            modifier = Modifier.height(24.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
        
        // Valore attuale (più grande e in evidenza)
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = currentValue.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
        
        // Valore successivo (più piccolo e trasparente)
        val nextIndex = (values.indexOf(currentValue) + 1) % values.size
        val nextValue = if (nextIndex < values.size) values[nextIndex] else values.first()
        Text(
            text = nextValue.toString(),
            modifier = Modifier.height(24.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            textAlign = TextAlign.Center
        )
        
        // Pulsanti Su/Giù
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Pulsante Giù
            IconButton(
                onClick = {
                    val currentIndex = values.indexOf(currentValue)
                    val newIndex = if (currentIndex <= 0) values.size - 1 else currentIndex - 1
                    currentValue = values[newIndex]
                    onValueChange(currentValue)
                },
                modifier = Modifier.size(24.dp)
            ) {
                Text("▲", color = MaterialTheme.colorScheme.primary)
            }
            
            // Pulsante Su
            IconButton(
                onClick = {
                    val currentIndex = values.indexOf(currentValue)
                    val newIndex = (currentIndex + 1) % values.size
                    currentValue = values[newIndex]
                    onValueChange(currentValue)
                },
                modifier = Modifier.size(24.dp)
            ) {
                Text("▼", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
} 