package com.valcan.tt.data.repository

import com.valcan.tt.data.dao.ClothesDao
import com.valcan.tt.data.model.Clothes
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ClothesRepository @Inject constructor(
    private val clothesDao: ClothesDao
) {
    fun getAllClothes(): Flow<List<Clothes>> = clothesDao.getAllClothes()
    
    fun getClothesByWardrobe(wardrobeId: Long): Flow<List<Clothes>> = 
        clothesDao.getClothesByWardrobe(wardrobeId)
    
    suspend fun getClothById(clothId: Long): Clothes? = clothesDao.getClothById(clothId)
    
    suspend fun insertCloth(cloth: Clothes): Long = clothesDao.insertCloth(cloth)
    
    suspend fun updateCloth(cloth: Clothes) = clothesDao.updateCloth(cloth)
    
    suspend fun deleteCloth(cloth: Clothes) = clothesDao.deleteCloth(cloth)
} 