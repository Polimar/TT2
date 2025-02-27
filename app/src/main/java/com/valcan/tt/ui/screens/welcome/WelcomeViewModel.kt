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
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _usersState = MutableStateFlow<UsersState>(UsersState.Loading)
    val usersState: StateFlow<UsersState> = _usersState.asStateFlow()

    private val _showNewUserDialog = MutableStateFlow(false)
    val showNewUserDialog: StateFlow<Boolean> = _showNewUserDialog.asStateFlow()

    init {
        checkExistingUsers()
    }

    private fun checkExistingUsers() {
        viewModelScope.launch {
            userRepository.getAllUsers().collect { users ->
                _usersState.value = when {
                    users.isEmpty() -> {
                        _showNewUserDialog.value = true
                        UsersState.NoUsers
                    }
                    users.size == 1 -> UsersState.SingleUser(users.first())
                    else -> UsersState.MultipleUsers(users)
                }
            }
        }
    }

    fun createNewUser(name: String, birthday: Date, gender: String) {
        viewModelScope.launch {
            val newUser = User(
                name = name,
                birthday = birthday,
                gender = gender
            )
            userRepository.insertUser(newUser)
            _showNewUserDialog.value = false
        }
    }
}

sealed class UsersState {
    object Loading : UsersState()
    object NoUsers : UsersState()
    data class SingleUser(val user: User) : UsersState()
    data class MultipleUsers(val users: List<User>) : UsersState()
} 