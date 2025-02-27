package com.valcan.tt.data.repository

import com.valcan.tt.data.dao.ShoesDao
import com.valcan.tt.data.model.Shoes
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ShoesRepository @Inject constructor(
    private val shoesDao: ShoesDao
) {
    fun getAllShoes(): Flow<List<Shoes>> = shoesDao.getAllShoes()
    
    fun getShoesByWardrobe(wardrobeId: Long): Flow<List<Shoes>> = 
        shoesDao.getShoesByWardrobe(wardrobeId)
    
    fun getShoesByUser(userId: Long): Flow<List<Shoes>> = 
        shoesDao.getShoesByUser(userId)
    
    suspend fun getShoeById(shoeId: Long): Shoes? = shoesDao.getShoeById(shoeId)
    
    suspend fun insertShoe(shoe: Shoes): Long = shoesDao.insertShoe(shoe)
    
    suspend fun updateShoe(shoe: Shoes) = shoesDao.updateShoe(shoe)
    
    suspend fun deleteShoe(shoe: Shoes) = shoesDao.deleteShoe(shoe)
} 