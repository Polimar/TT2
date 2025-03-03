package com.valcan.tt.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valcan.tt.data.repository.ClothesRepository
import com.valcan.tt.data.repository.ShoesRepository
import com.valcan.tt.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val clothesRepository: ClothesRepository,
    private val shoesRepository: ShoesRepository
) : ViewModel() {

    val currentUser = userRepository.getCurrentUser()

    val userClothes: StateFlow<Int> = combine(
        clothesRepository.getAllClothes(),
        currentUser
    ) { clothes, user ->
        clothes.count { it.userId == user?.userId }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val userShoes: StateFlow<Int> = combine(
        shoesRepository.getAllShoes(),
        currentUser
    ) { shoes, user ->
        shoes.count { it.userId == user?.userId }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )
} 