package com.valcan.tt.ui.screens.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valcan.tt.data.model.User
import com.valcan.tt.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _hasExistingUsers = MutableStateFlow<Boolean?>(null)
    val hasExistingUsers: StateFlow<Boolean?> = _hasExistingUsers.asStateFlow()

    init {
        checkExistingUsers()
    }

    private fun checkExistingUsers() {
        viewModelScope.launch {
            userRepository.getAllUsers().collect { users ->
                _hasExistingUsers.value = users.isNotEmpty()
            }
        }
    }
} 