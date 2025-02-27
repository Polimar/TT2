package com.valcan.tt.data.dao

import androidx.room.*
import com.valcan.tt.data.model.Wardrobe
import kotlinx.coroutines.flow.Flow

@Dao
interface WardrobeDao {
    @Query("SELECT * FROM wardrobes")
    fun getAllWardrobes(): Flow<List<Wardrobe>>
    
    @Query("SELECT * FROM wardrobes WHERE wardrobeId = :wardrobeId")
    suspend fun getWardrobeById(wardrobeId: Long): Wardrobe?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWardrobe(wardrobe: Wardrobe): Long
    
    @Update
    suspend fun updateWardrobe(wardrobe: Wardrobe)
    
    @Delete
    suspend fun deleteWardrobe(wardrobe: Wardrobe)
} 