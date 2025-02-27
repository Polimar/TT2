package com.valcan.tt.data.repository

import com.valcan.tt.data.dao.WardrobeDao
import com.valcan.tt.data.model.Wardrobe
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WardrobeRepository @Inject constructor(
    private val wardrobeDao: WardrobeDao
) {
    fun getAllWardrobes(): Flow<List<Wardrobe>> = wardrobeDao.getAllWardrobes()
    
    suspend fun getWardrobeById(wardrobeId: Long): Wardrobe? = wardrobeDao.getWardrobeById(wardrobeId)
    
    suspend fun insertWardrobe(wardrobe: Wardrobe): Long = wardrobeDao.insertWardrobe(wardrobe)
    
    suspend fun updateWardrobe(wardrobe: Wardrobe) = wardrobeDao.updateWardrobe(wardrobe)
    
    suspend fun deleteWardrobe(wardrobe: Wardrobe) = wardrobeDao.deleteWardrobe(wardrobe)
} 