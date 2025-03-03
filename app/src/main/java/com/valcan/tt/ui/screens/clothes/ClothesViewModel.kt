package com.valcan.tt.ui.screens.clothes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valcan.tt.data.model.Clothes
import com.valcan.tt.data.model.Wardrobe
import com.valcan.tt.data.repository.ClothesRepository
import com.valcan.tt.data.repository.WardrobeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ClothesViewModel @Inject constructor(
    private val clothesRepository: ClothesRepository,
    private val wardrobeRepository: WardrobeRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val clothes: StateFlow<List<Clothes>> = _searchQuery
        .debounce(300)
        .combine(clothesRepository.getAllClothes()) { query, clothes ->
            if (query.isBlank()) {
                clothes
            } else {
                clothes.filter { it.name.contains(query, ignoreCase = true) }
            }
        }
        .stateIn(
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

    fun addCloth(cloth: Clothes) {
        viewModelScope.launch {
            clothesRepository.insertCloth(cloth)
        }
    }

    fun updateCloth(cloth: Clothes) {
        viewModelScope.launch {
            clothesRepository.updateCloth(cloth)
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