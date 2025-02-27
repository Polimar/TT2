package com.valcan.tt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valcan.tt.data.model.Shoes
import com.valcan.tt.data.repository.ShoesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShoesViewModel @Inject constructor(
    private val shoesRepository: ShoesRepository
) : ViewModel() {

    val allShoes: StateFlow<List<Shoes>> = shoesRepository.getAllShoes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun getShoesByWardrobe(wardrobeId: Long): StateFlow<List<Shoes>> = 
        shoesRepository.getShoesByWardrobe(wardrobeId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun getShoesByUser(userId: Long): StateFlow<List<Shoes>> = 
        shoesRepository.getShoesByUser(userId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun addShoe(shoe: Shoes) {
        viewModelScope.launch {
            shoesRepository.insertShoe(shoe)
        }
    }

    fun updateShoe(shoe: Shoes) {
        viewModelScope.launch {
            shoesRepository.updateShoe(shoe)
        }
    }

    fun deleteShoe(shoe: Shoes) {
        viewModelScope.launch {
            shoesRepository.deleteShoe(shoe)
        }
    }
} 