package com.valcan.tt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valcan.tt.data.model.Wardrobe
import com.valcan.tt.data.repository.WardrobeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WardrobeViewModel @Inject constructor(
    private val wardrobeRepository: WardrobeRepository
) : ViewModel() {

    val wardrobes: StateFlow<List<Wardrobe>> = wardrobeRepository.getAllWardrobes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addWardrobe(wardrobe: Wardrobe) {
        viewModelScope.launch {
            wardrobeRepository.insertWardrobe(wardrobe)
        }
    }

    fun updateWardrobe(wardrobe: Wardrobe) {
        viewModelScope.launch {
            wardrobeRepository.updateWardrobe(wardrobe)
        }
    }

    fun deleteWardrobe(wardrobe: Wardrobe) {
        viewModelScope.launch {
            wardrobeRepository.deleteWardrobe(wardrobe)
        }
    }
} 