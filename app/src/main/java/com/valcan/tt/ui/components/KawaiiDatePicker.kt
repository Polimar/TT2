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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.*
import com.valcan.tt.R


/**
 * Componente KawaiiDatePicker: un selettore di data in stile kawaii.
 * Permette all'utente di selezionare una data (giorno, mese, anno) attraverso tre colonne scorrevoli.
 * 
 * @param initialDate data iniziale, se non fornita viene usata la data corrente
 * @param onDateSelected callback che viene invocata quando l'utente seleziona una data
 * @param onDismiss callback che viene invocata quando l'utente chiude il selettore
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KawaiiDatePicker(
    initialDate: Date? = null,
    onDateSelected: (Date) -> Unit,  // Callback chiamata quando l'utente conferma la selezione
    onDismiss: () -> Unit  // Callback chiamata quando l'utente annulla la selezione
) {
    val calendar = Calendar.getInstance()
    
    // Inizializza con la data corrente o con la data passata
    initialDate?.let {
        calendar.time = it
    }
    
    var selectedDay by remember { mutableStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var selectedMonth by remember { mutableStateOf(calendar.get(Calendar.MONTH)) }
    var selectedYear by remember { mutableStateOf(calendar.get(Calendar.YEAR)) }
    
    // Lista dei giorni (1-31)
    val days = (1..31).toList()
    
    // Lista dei mesi (0-11 => Gennaio-Dicembre)
    val months = listOf(
        "Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno",
        "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre"
    )
    
    // Lista degli anni (ultimi 100 anni)
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val years = (currentYear - 100..currentYear).toList().reversed()
    
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
            selectedMonth = index  // Aggiorniamo il mese selezionato
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
                stringResource(R.string.edit_profile_birthday),
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
                            stringResource(R.string.date_day),
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
                            stringResource(R.string.date_month),
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
                            text = stringResource(R.string.date_year),
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
                                        color = if (months.indexOf(month) == selectedMonth) 
                                            MaterialTheme.colorScheme.primary  // Colore primario per il mese selezionato
                                        else 
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),  // Colore con opacità variabile
                                        fontWeight = if (months.indexOf(month) == selectedMonth) FontWeight.Bold else FontWeight.Normal  // Grassetto se selezionato
                                    )
                                }
                            }
                        }
                    }
                    
                    // Cilindro per gli anni
                Box(
                    modifier = Modifier
                            .weight(1.2f)  // Occupa 1.2 parti di spazio orizzontale (leggermente più largo per ospitare 4 cifre)
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
            // Pulsante di conferma (Conferma/Seleziona)
            TextButton(
                onClick = {
                    // Impostiamo la data selezionata
                    calendar.set(selectedYear, selectedMonth, 1)
                    // Aggiustiamo il giorno per evitare problemi di mesi con giorni diversi
                    val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                    val adjustedDay = if (selectedDay > maxDay) maxDay else selectedDay
                    calendar.set(Calendar.DAY_OF_MONTH, adjustedDay)
                    onDateSelected(calendar.time)  // Chiamiamo la callback con la data selezionata
                    onDismiss()  // Chiudiamo il dialog
                }
            ) {
                Text(stringResource(R.string.action_confirm))
            }
        },
        dismissButton = {
            // Pulsante di annullamento (Annulla)
            TextButton(
                onClick = onDismiss  // Chiude il dialog
            ) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
} 