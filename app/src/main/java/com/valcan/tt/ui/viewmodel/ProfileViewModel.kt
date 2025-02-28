package com.valcan.tt.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.valcan.tt.data.model.User
import com.valcan.tt.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    val users: Flow<List<User>> = userRepository.getAllUsers()
    
    fun updateCurrentUser(user: User) {
        userRepository.updateCurrentUser(user)
    }
} 