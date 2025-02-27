package com.valcan.tt.data.dao

import androidx.room.*
import com.valcan.tt.data.model.Clothes
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothesDao {
    @Query("SELECT * FROM clothes")
    fun getAllClothes(): Flow<List<Clothes>>
    
    @Query("SELECT * FROM clothes WHERE wardrobeId = :wardrobeId")
    fun getClothesByWardrobe(wardrobeId: Long): Flow<List<Clothes>>
    
    @Query("SELECT * FROM clothes WHERE id = :clothId")
    suspend fun getClothById(clothId: Long): Clothes?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCloth(cloth: Clothes): Long
    
    @Update
    suspend fun updateCloth(cloth: Clothes)
    
    @Delete
    suspend fun deleteCloth(cloth: Clothes)
} 