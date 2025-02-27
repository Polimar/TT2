package com.valcan.tt.ui.screens.clothes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valcan.tt.data.model.Clothes
import com.valcan.tt.data.repository.ClothesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClothDetailViewModel @Inject constructor(
    private val clothesRepository: ClothesRepository
) : ViewModel() {

    private val _cloth = MutableStateFlow<Clothes?>(null)
    val cloth: StateFlow<Clothes?> = _cloth.asStateFlow()

    fun loadCloth(clothId: Long) {
        viewModelScope.launch {
            _cloth.value = clothesRepository.getClothById(clothId)
        }
    }
} 