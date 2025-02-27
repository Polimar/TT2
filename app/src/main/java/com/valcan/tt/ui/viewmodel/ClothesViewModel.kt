package com.valcan.tt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valcan.tt.data.model.Clothes
import com.valcan.tt.data.repository.ClothesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClothesViewModel @Inject constructor(
    private val clothesRepository: ClothesRepository
) : ViewModel() {

    val allClothes: StateFlow<List<Clothes>> = clothesRepository.getAllClothes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getClothesByWardrobe(wardrobeId: Long): StateFlow<List<Clothes>> = 
        clothesRepository.getClothesByWardrobe(wardrobeId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

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
} 