package com.valcan.tt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valcan.tt.data.repository.UserRepository
import com.valcan.tt.data.repository.ClothesRepository
import com.valcan.tt.data.repository.ShoesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val clothesRepository: ClothesRepository,
    private val shoesRepository: ShoesRepository
) : ViewModel() {
    
    val currentUser = userRepository.getCurrentUser()
    val totalClothes = clothesRepository.getTotalClothesCount()
    val totalShoes = shoesRepository.getTotalShoesCount()

    init {
        println("DEBUG: HomeViewModel initialized with currentUser: ${currentUser.value}")
        updateCounts()
    }

    private fun updateCounts() {
        viewModelScope.launch {
            // Aggiorna il conteggio dei vestiti
            clothesRepository.getAllClothes().collect { clothes ->
                clothesRepository.updateTotalClothes(clothes.size)
            }
        }

        viewModelScope.launch {
            // Aggiorna il conteggio delle scarpe
            shoesRepository.getAllShoes().collect { shoes ->
                shoesRepository.updateTotalShoes(shoes.size)
            }
        }
    }
} 