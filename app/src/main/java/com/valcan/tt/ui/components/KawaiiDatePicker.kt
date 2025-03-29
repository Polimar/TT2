package com.valcan.tt.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.*


/**
 * Componente KawaiiDatePicker: un selettore di data in stile kawaii.
 * Permette all'utente di selezionare una data (giorno, mese, anno) attraverso tre colonne scorrevoli.
 * 
 * @param onDateSelected callback che viene invocata quando l'utente seleziona una data
 * @param onDismiss callback che viene invocata quando l'utente chiude il selettore
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KawaiiDatePicker(
    //initialDate: Date = Date(),  // Parametro commentato, potremmo usarlo in futuro per inizializzare la data
    onDateSelected: (Date) -> Unit,  // Callback chiamata quando l'utente conferma la selezione
    onDismiss: () -> Unit  // Callback chiamata quando l'utente annulla la selezione
) {
    // Otteniamo l'anno corrente per limitare la selezione degli anni
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    
    // Inizializziamo con valori di default per giorno, mese e anno
    var selectedDay by remember { mutableStateOf(15) }
    var selectedMonth by remember { mutableStateOf(6) }
    var selectedYear by remember { mutableStateOf(2000) }
    
    // Creiamo le liste di valori per anni, mesi e giorni
    // Replichiamo le liste per mesi e giorni tre volte per permettere lo scorrimento infinito
    val years = (1936..currentYear).toList()  // Lista di anni dal 1936 ad oggi
    val months = (1..12).toList() + (1..12).toList() + (1..12).toList()  // Lista di mesi replicata tre volte
    val days = (1..31).toList() + (1..31).toList() + (1..31).toList()  // Lista di giorni replicata tre volte
    
    // Stati per gestire la posizione iniziale e lo scorrimento delle liste
    val dayState = rememberLazyListState(initialFirstVisibleItemIndex = 31)  // Inizia dal secondo blocco di giorni
    val monthState = rememberLazyListState(initialFirstVisibleItemIndex = 12)  // Inizia dal secondo blocco di mesi
    val yearState = rememberLazyListState(initialFirstVisibleItemIndex = (years.size - 1) / 2)  // Inizia dalla metà della lista degli anni
    
    // Aggiorniamo i valori selezionati quando cambia la posizione di scorrimento
    LaunchedEffect(dayState.firstVisibleItemIndex) {
        // +1 perché vogliamo il secondo elemento visibile (quello centrale)
        val index = dayState.firstVisibleItemIndex + 1
        if (index < days.size) {
            selectedDay = days[index]  // Aggiorniamo il giorno selezionato
        }
    }
    
    // Aggiorniamo il mese selezionato quando cambia la posizione di scorrimento
    LaunchedEffect(monthState.firstVisibleItemIndex) {
        val index = monthState.firstVisibleItemIndex + 1
        if (index < months.size) {
            selectedMonth = months[index]  // Aggiorniamo il mese selezionato
        }
    }
    
    // Aggiorniamo l'anno selezionato quando cambia la posizione di scorrimento
    LaunchedEffect(yearState.firstVisibleItemIndex) {
        val index = yearState.firstVisibleItemIndex + 1
        if (index < years.size) {
            selectedYear = years[index]  // Aggiorniamo l'anno selezionato
        }
    }
    
    // Dialog che contiene il selettore di data
    AlertDialog(
        onDismissRequest = onDismiss,  // Chiamato quando l'utente chiude il dialog
        title = {
            // Titolo del dialog
            Text(
                "Seleziona la tua\ndata di nascita",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center  // Centrato orizzontalmente
            )
        },
        text = {
            // Contenuto principale del dialog
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally  // Centrato orizzontalmente
            ) {
                // Prima riga: etichette Giorno, Mese, Anno
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),  // Aumentiamo lo spazio sotto le etichette
                    horizontalArrangement = Arrangement.SpaceEvenly  // Distribuisce lo spazio uniformemente
                ) {
                    // Etichetta Giorno
                    Box(
                        modifier = Modifier.weight(1f),  // Occupa 1 parte di spazio orizzontale
                        contentAlignment = Alignment.Center  // Centra il contenuto
                    ) {
                        Text(
                            text = "Giorno",
                            style = MaterialTheme.typography.bodyLarge,  // Testo più grande
                            color = MaterialTheme.colorScheme.primary,  // Colore primario
                            fontWeight = FontWeight.Bold  // Testo in grassetto
                        )
                    }
                    
                    // Etichetta Mese
                    Box(
                        modifier = Modifier.weight(1f),  // Occupa 1 parte di spazio orizzontale
                        contentAlignment = Alignment.Center  // Centra il contenuto
                    ) {
                        Text(
                            text = "Mese",
                            style = MaterialTheme.typography.bodyLarge,  // Testo più grande
                            color = MaterialTheme.colorScheme.primary,  // Colore primario
                            fontWeight = FontWeight.Bold  // Testo in grassetto
                        )
                    }
                    
                    // Etichetta Anno
                    Box(
                        modifier = Modifier.weight(1.2f),  // Occupa 1.2 parti di spazio orizzontale (leggermente più largo)
                        contentAlignment = Alignment.Center  // Centra il contenuto
                    ) {
                        Text(
                            text = "Anno",
                            style = MaterialTheme.typography.bodyLarge,  // Testo più grande
                            color = MaterialTheme.colorScheme.primary,  // Colore primario
                            fontWeight = FontWeight.Bold  // Testo in grassetto
                        )
                    }
                }
                
                // Seconda riga: selettori numerici
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),  // Aumentiamo l'altezza per vedere più numeri
                    horizontalArrangement = Arrangement.SpaceEvenly  // Distribuisce lo spazio uniformemente
                ) {
                    // Cilindro per i giorni
                    Box(
                        modifier = Modifier
                            .weight(1f)  // Occupa 1 parte di spazio orizzontale
                            .fillMaxHeight()  // Occupa tutta l'altezza disponibile
                    ) {
                        // Rettangolo centrale evidenziato (zona colorata)
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)  // Allineato al centro
                                .fillMaxWidth()  // Occupa tutta la larghezza disponibile
                                .height(50.dp)  // Altezza fissa
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))  // Sfondo colorato semi-trasparente
                        )
                        
                        // Lista scorrevole di numeri (giorni)
                        LazyColumn(
                            state = dayState,  // Stato della lista per gestire lo scorrimento
                            modifier = Modifier.fillMaxSize(),  // Occupa tutto lo spazio disponibile
                            horizontalAlignment = Alignment.CenterHorizontally,  // Centrato orizzontalmente
                            contentPadding = PaddingValues(vertical = 10.dp)  // Padding verticale per mostrare elementi oltre i bordi visibili
                        ) {
                            items(days) { day ->
                                // Elemento della lista (giorno)
                                Box(
                                    modifier = Modifier
                                        .height(50.dp)  // Altezza fissa
                                        .fillMaxWidth(),  // Occupa tutta la larghezza disponibile
                                    contentAlignment = Alignment.Center  // Centra il contenuto
                                ) {
                                    // Testo del giorno
                                    Text(
                                        text = day.toString(),
                                        style = MaterialTheme.typography.titleMedium,  // Stile titolo medio
                                        color = if (day == selectedDay) 
                                            MaterialTheme.colorScheme.primary  // Colore primario per il giorno selezionato
                                        else 
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),  // Colore con opacità variabile
                                        fontWeight = if (day == selectedDay) FontWeight.Bold else FontWeight.Normal  // Grassetto se selezionato
                                    )
                                }
                            }
                        }
                    }
                    
                    // Cilindro per i mesi
                    Box(
                        modifier = Modifier
                            .weight(1f)  // Occupa 1 parte di spazio orizzontale
                            .fillMaxHeight()  // Occupa tutta l'altezza disponibile
                    ) {
                        // Rettangolo centrale evidenziato (zona colorata)
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)  // Allineato al centro
                                .fillMaxWidth()  // Occupa tutta la larghezza disponibile
                                .height(50.dp)  // Altezza fissa
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))  // Sfondo colorato semi-trasparente
                        )
                        
                        // Lista scorrevole di numeri (mesi)
                        LazyColumn(
                            state = monthState,  // Stato della lista per gestire lo scorrimento
                            modifier = Modifier.fillMaxSize(),  // Occupa tutto lo spazio disponibile
                            horizontalAlignment = Alignment.CenterHorizontally,  // Centrato orizzontalmente
                            contentPadding = PaddingValues(vertical = 10.dp)  // Padding verticale per mostrare elementi oltre i bordi visibili
                        ) {
                            items(months) { month ->
                                // Elemento della lista (mese)
                                Box(
                                    modifier = Modifier
                                        .height(50.dp)  // Altezza fissa
                                        .fillMaxWidth(),  // Occupa tutta la larghezza disponibile
                                    contentAlignment = Alignment.Center  // Centra il contenuto
                                ) {

                                    // Testo del mese
                                    Text(
                                        text = month.toString(),
                                        style = MaterialTheme.typography.titleMedium,  // Stile titolo medio
                                        color = if (month == selectedMonth) 
                                            MaterialTheme.colorScheme.primary  // Colore primario per il mese selezionato
                                        else 
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),  // Colore con opacità variabile
                                        fontWeight = if (month == selectedMonth) FontWeight.Bold else FontWeight.Normal  // Grassetto se selezionato
                                    )
                                }
                            }
                        }
                    }
                    
                    // Cilindro per gli anni
                    Box(
                        modifier = Modifier
                            .weight(1.2f)  // Occupa 1.2 parti di spazio orizzontale (leggermente più largo)
                            .fillMaxHeight()  // Occupa tutta l'altezza disponibile
                    ) {
                        // Rettangolo centrale evidenziato (zona colorata)
                        Box(
                            modifier = Modifier
                                .align(Alignment.Center)  // Allineato al centro
                                .fillMaxWidth()  // Occupa tutta la larghezza disponibile
                                .height(50.dp)  // Altezza fissa
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))  // Sfondo colorato semi-trasparente
                        )
                        
                        // Lista scorrevole di numeri (anni)
                        LazyColumn(
                            state = yearState,  // Stato della lista per gestire lo scorrimento
                            modifier = Modifier.fillMaxSize(),  // Occupa tutto lo spazio disponibile
                            horizontalAlignment = Alignment.CenterHorizontally,  // Centrato orizzontalmente
                            contentPadding = PaddingValues(vertical = 10.dp)  // Padding verticale per mostrare elementi oltre i bordi visibili
                        ) {
                            items(years) { year ->
                                // Elemento della lista (anno)
                                Box(
                                    modifier = Modifier
                                        .height(50.dp)  // Altezza fissa
                                        .fillMaxWidth(),  // Occupa tutta la larghezza disponibile
                                    contentAlignment = Alignment.Center  // Centra il contenuto
                                ) {

                                    // Testo dell'anno
                                    Text(
                                        text = year.toString(),
                                        style = MaterialTheme.typography.titleMedium,  // Stile titolo medio
                                        color = if (year == selectedYear) 
                                            MaterialTheme.colorScheme.primary  // Colore primario per l'anno selezionato
                                        else 
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),  // Colore con opacità variabile
                                        fontWeight = if (year == selectedYear) FontWeight.Bold else FontWeight.Normal  // Grassetto se selezionato
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            // Pulsante di conferma
            KawaiiButton(onClick = {
                val calendar = Calendar.getInstance()  // Creiamo un nuovo calendario
                
                // Aggiunto controllo per giorni validi in base al mese
                // (per gestire febbraio, mesi da 30 giorni, ecc.)
                val adjustedDay = when (selectedMonth) {
                    2 -> selectedDay.coerceAtMost(if (selectedYear % 4 == 0 && (selectedYear % 100 != 0 || selectedYear % 400 == 0)) 29 else 28)  // Febbraio con anni bisestili
                    4, 6, 9, 11 -> selectedDay.coerceAtMost(30)  // Mesi da 30 giorni
                    else -> selectedDay  // Altri mesi da 31 giorni
                }
                
                // Impostiamo la data selezionata
                calendar.set(selectedYear, selectedMonth - 1, adjustedDay)  // Il mese in Calendar è 0-based
                onDateSelected(calendar.time)  // Restituiamo la data selezionata
            }) {
                Text("Conferma")  // Testo del pulsante
            }
        },
        dismissButton = {
            // Pulsante di chiusura
            TextButton(onClick = onDismiss) {  // Chiamato quando l'utente annulla
                Text("Annulla")  // Testo del pulsante
            }
        }
    )
}