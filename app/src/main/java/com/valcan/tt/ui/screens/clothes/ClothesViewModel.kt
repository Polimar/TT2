package com.valcan.tt.ui.screens.clothes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valcan.tt.data.model.Clothes
import com.valcan.tt.data.model.Wardrobe
import com.valcan.tt.data.repository.ClothesRepository
import com.valcan.tt.data.repository.UserRepository
import com.valcan.tt.data.repository.WardrobeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@OptIn(kotlinx.coroutines.FlowPreview::class)
@HiltViewModel
class ClothesViewModel @Inject constructor(
    private val clothesRepository: ClothesRepository,
    private val wardrobeRepository: WardrobeRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    
    // Gestione delle categorie
    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories = _categories.asStateFlow()

    init {
        // Carica le categorie esistenti dai vestiti
        viewModelScope.launch {
            clothesRepository.getAllClothes().collect { allClothes ->
                val uniqueCategories = allClothes
                    .map { it.category }
                    .filter { it.isNotBlank() }
                    .distinct()
                    .sorted()
                _categories.value = uniqueCategories
            }
        }
    }

    val clothes: StateFlow<List<Clothes>> = combine(
        _searchQuery.debounce(300),
        clothesRepository.getAllClothes(),
        userRepository.getCurrentUser()
    ) { query, clothes, currentUser ->
        clothes.filter { cloth ->
            cloth.userId == currentUser?.userId && (query.isBlank() || cloth.name.contains(query, ignoreCase = true))
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
    
    fun addCategory(category: String) {
        if (category.isBlank() || _categories.value.contains(category)) return
        
        _categories.value = _categories.value + category
    }
    
    fun removeCategory(category: String) {
        _categories.value = _categories.value.filter { it != category }
    }

    fun addCloth(cloth: Clothes) {
        viewModelScope.launch {
            userRepository.getCurrentUser().first()?.let { currentUser ->
                clothesRepository.insertCloth(cloth.copy(userId = currentUser.userId))
            }
        }
    }

    fun updateCloth(cloth: Clothes) {
        viewModelScope.launch {
            userRepository.getCurrentUser().first()?.let { currentUser ->
                clothesRepository.updateCloth(cloth.copy(userId = currentUser.userId))
            }
        }
    }

    fun deleteCloth(cloth: Clothes) {
        viewModelScope.launch {
            clothesRepository.deleteCloth(cloth)
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
} 