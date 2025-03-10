package com.valcan.tt.data.repository

import com.valcan.tt.data.dao.UserDao
import com.valcan.tt.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton  // Aggiungiamo questa annotazione per assicurarci che ci sia una sola istanza
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {
    private val _currentUser = MutableStateFlow<User?>(null)
    
    fun getAllUsers(): Flow<List<User>> = userDao.getAllUsers()
    
    suspend fun getUserById(userId: Long): User? = userDao.getUserById(userId)
    
    suspend fun insertUser(user: User): Long = userDao.insertUser(user)
    
    suspend fun updateUser(user: User) = userDao.updateUser(user)
    
    suspend fun deleteUser(user: User) = userDao.deleteUser(user)
    
    fun getCurrentUser(): StateFlow<User?> = _currentUser
    
    fun updateCurrentUser(user: User) {
        println("DEBUG: Updating current user in repository: $user")
        _currentUser.value = user
    }
} 