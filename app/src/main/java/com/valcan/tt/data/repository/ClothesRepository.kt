package com.valcan.tt.data.repository

import com.valcan.tt.data.dao.ClothesDao
import com.valcan.tt.data.model.Clothes
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClothesRepository @Inject constructor(
    private val clothesDao: ClothesDao
) {
    private val _totalClothes = MutableStateFlow(0)
    
    fun getAllClothes(): Flow<List<Clothes>> = clothesDao.getAllClothes()
    
    fun getClothesByWardrobe(wardrobeId: Long): Flow<List<Clothes>> = 
        clothesDao.getClothesByWardrobe(wardrobeId)
    
    suspend fun getClothById(clothId: Long): Clothes? = clothesDao.getClothById(clothId)
    
    suspend fun insertCloth(cloth: Clothes): Long = clothesDao.insertCloth(cloth)
    
    suspend fun updateCloth(cloth: Clothes) = clothesDao.updateCloth(cloth)
    
    suspend fun deleteCloth(cloth: Clothes) = clothesDao.deleteCloth(cloth)
    
    fun getTotalClothesCount(): StateFlow<Int> = _totalClothes
    
    fun updateTotalClothes(count: Int) {
        _totalClothes.value = count
    }
} 