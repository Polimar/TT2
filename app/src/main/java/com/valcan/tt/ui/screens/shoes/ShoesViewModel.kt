package com.valcan.tt.ui.screens.shoes

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valcan.tt.data.model.Shoes
import com.valcan.tt.data.model.Wardrobe
import com.valcan.tt.data.repository.ShoesRepository
import com.valcan.tt.data.repository.UserRepository
import com.valcan.tt.data.repository.WardrobeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@OptIn(kotlinx.coroutines.FlowPreview::class)
@HiltViewModel
class ShoesViewModel @Inject constructor(
    private val shoesRepository: ShoesRepository,
    private val wardrobeRepository: WardrobeRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Gestione dei tipi
    private val _types = MutableStateFlow<List<String>>(emptyList())
    val types = _types.asStateFlow()

    // Gestione dell'immagine dalla galleria
    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri = _selectedImageUri.asStateFlow()

    init {
        // Carica i tipi esistenti dalle scarpe
        viewModelScope.launch {
            shoesRepository.getAllShoes().collect { allShoes ->
                val uniqueTypes = allShoes
                    .mapNotNull { it.type }
                    .filter { it.isNotBlank() }
                    .distinct()
                    .sorted()
                _types.value = uniqueTypes
            }
        }
    }

    fun addType(type: String) {
        if (type.isBlank() || _types.value.contains(type)) return
        
        _types.value = _types.value + type
    }
    
    fun removeType(type: String) {
        _types.value = _types.value.filter { it != type }
    }

    val shoes: StateFlow<List<Shoes>> = combine(
        _searchQuery.debounce(300),
        shoesRepository.getAllShoes(),
        userRepository.getCurrentUser()
    ) { query, shoes, currentUser ->
        shoes.filter { shoe ->
            shoe.userId == currentUser?.userId && (query.isBlank() || shoe.name.contains(query, ignoreCase = true))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val wardrobes: StateFlow<List<Wardrobe>> = wardrobeRepository.getAllWardrobes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun addShoe(shoe: Shoes) {
        viewModelScope.launch {
            userRepository.getCurrentUser().first()?.let { currentUser ->
                shoesRepository.insertShoe(shoe.copy(userId = currentUser.userId))
            }
        }
    }

    fun updateShoe(shoe: Shoes) {
        viewModelScope.launch {
            userRepository.getCurrentUser().first()?.let { currentUser ->
                shoesRepository.updateShoe(shoe.copy(userId = currentUser.userId))
            }
        }
    }

    fun deleteShoe(shoe: Shoes) {
        viewModelScope.launch {
            shoesRepository.deleteShoe(shoe)
        }
    }

    fun addWardrobe(name: String, description: String?) {
        viewModelScope.launch {
            wardrobeRepository.insertWardrobe(
                Wardrobe(
                    name = name,
                    description = description,
                    createdAt = Date()
                )
            )
        }
    }

    fun pickImageFromGallery() {
        // Questo metodo verrà chiamato quando l'utente clicca sull'icona della galleria
        // L'implementazione reale avverrà tramite un Content Resolver o Intent nel componente UI
        // qui settiamo solo uno stato per comunicare l'intenzione
    }
    
    fun setSelectedImageUri(uri: Uri?) {
        _selectedImageUri.value = uri
    }
} 