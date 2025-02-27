package com.valcan.tt.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valcan.tt.data.repository.ClothesRepository
import com.valcan.tt.data.repository.ShoesRepository
import com.valcan.tt.data.repository.WardrobeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val wardrobeRepository: WardrobeRepository,
    private val clothesRepository: ClothesRepository,
    private val shoesRepository: ShoesRepository
) : ViewModel() {

    val statistics: StateFlow<HomeStatistics> = kotlinx.coroutines.flow.combine(
        wardrobeRepository.getAllWardrobes(),
        clothesRepository.getAllClothes(),
        shoesRepository.getAllShoes()
    ) { wardrobes, clothes, shoes ->
        HomeStatistics(
            wardrobeCount = wardrobes.size,
            clothesCount = clothes.size,
            shoesCount = shoes.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeStatistics(0, 0, 0)
    )
}

data class HomeStatistics(
    val wardrobeCount: Int,
    val clothesCount: Int,
    val shoesCount: Int
) 