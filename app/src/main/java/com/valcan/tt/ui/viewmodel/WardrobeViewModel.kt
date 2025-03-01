package com.valcan.tt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valcan.tt.data.model.Wardrobe
import com.valcan.tt.data.repository.WardrobeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class WardrobeViewModel @Inject constructor(
    private val wardrobeRepository: WardrobeRepository
) : ViewModel() {
    
    val wardrobes: Flow<List<Wardrobe>> = wardrobeRepository.getAllWardrobes()
    
    private val _showEditDialog = MutableStateFlow<Wardrobe?>(null)
    val showEditDialog: StateFlow<Wardrobe?> = _showEditDialog.asStateFlow()
    
    fun createWardrobe(name: String, description: String) {
        viewModelScope.launch {
            val newWardrobe = Wardrobe(
                name = name,
                description = description,
                createdAt = Date()
            )
            wardrobeRepository.insertWardrobe(newWardrobe)
        }
    }
    
    fun deleteWardrobe(wardrobe: Wardrobe) {
        viewModelScope.launch {
            wardrobeRepository.deleteWardrobe(wardrobe)
        }
    }
    
    fun showEditWardrobe(wardrobe: Wardrobe) {
        _showEditDialog.value = wardrobe
    }
    
    fun hideEditDialog() {
        _showEditDialog.value = null
    }
    
    fun updateWardrobe(wardrobeId: Long, name: String, description: String) {
        viewModelScope.launch {
            val updatedWardrobe = Wardrobe(
                wardrobeId = wardrobeId,
                name = name,
                description = description,
                createdAt = _showEditDialog.value?.createdAt ?: Date()
            )
            wardrobeRepository.updateWardrobe(updatedWardrobe)
            hideEditDialog()
        }
    }
} 