package com.valcan.tt.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.valcan.tt.data.model.User
import com.valcan.tt.data.repository.UserRepository
import com.valcan.tt.utils.LocaleHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    val users: Flow<List<User>> = userRepository.getAllUsers()
    
    private val _showEditDialog = MutableStateFlow<User?>(null)
    val showEditDialog: StateFlow<User?> = _showEditDialog.asStateFlow()
    
    private val _currentLanguage = MutableStateFlow<String?>(null)
    val currentLanguage: StateFlow<String?> = _currentLanguage.asStateFlow()
    
    fun initLanguage(context: Context) {
        _currentLanguage.value = LocaleHelper.getSelectedLanguage(context)
    }
    
    fun changeLanguage(context: Context, languageCode: String): Context {
        _currentLanguage.value = languageCode
        return LocaleHelper.setLocale(context, languageCode)
    }
    
    fun updateCurrentUser(user: User) {
        userRepository.updateCurrentUser(user)
    }

    fun createNewUser(name: String, birthday: Date, gender: String) {
        viewModelScope.launch {
            val newUser = User(
                name = name,
                birthday = birthday,
                gender = gender
            )
            userRepository.insertUser(newUser)
        }
    }
    
    fun deleteUser(user: User) {
        viewModelScope.launch {
            userRepository.deleteUser(user)
        }
    }
    
    fun showEditUser(user: User) {
        _showEditDialog.value = user
    }
    
    fun hideEditDialog() {
        _showEditDialog.value = null
    }
    
    fun updateUser(userId: Long, name: String, birthday: Date, gender: String) {
        viewModelScope.launch {
            val updatedUser = User(
                userId = userId,
                name = name,
                birthday = birthday,
                gender = gender
            )
            userRepository.updateUser(updatedUser)
        }
    }
} 