package com.valcan.tt.ui.screens.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valcan.tt.data.model.User
import com.valcan.tt.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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

    private val _navigationEvent = MutableSharedFlow<Boolean>()
    val navigationEvent = _navigationEvent.asSharedFlow()

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
                    users.size == 1 -> {
                        val singleUser = users.first()
                        userRepository.updateCurrentUser(singleUser)
                        _navigationEvent.emit(true)
                        UsersState.SingleUser(singleUser)
                    }
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
            val userId = userRepository.insertUser(newUser)
            userRepository.getUserById(userId)?.let { createdUser ->
                userRepository.updateCurrentUser(createdUser)
                _navigationEvent.emit(true)
            }
            _showNewUserDialog.value = false
        }
    }

    fun selectUser(user: User) {
        viewModelScope.launch {
            userRepository.updateCurrentUser(user)
            _navigationEvent.emit(true)
        }
    }
}

sealed class UsersState {
    object Loading : UsersState()
    object NoUsers : UsersState()
    data class SingleUser(val user: User) : UsersState()
    data class MultipleUsers(val users: List<User>) : UsersState()
} 