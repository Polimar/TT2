package com.valcan.tt.data.dao

import androidx.room.*
import com.valcan.tt.data.model.Shoes
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoesDao {
    @Query("SELECT * FROM shoes")
    fun getAllShoes(): Flow<List<Shoes>>
    
    @Query("SELECT * FROM shoes WHERE wardrobeId = :wardrobeId")
    fun getShoesByWardrobe(wardrobeId: Long): Flow<List<Shoes>>
    
    @Query("SELECT * FROM shoes WHERE userId = :userId")
    fun getShoesByUser(userId: Long): Flow<List<Shoes>>
    
    @Query("SELECT * FROM shoes WHERE id = :shoeId")
    suspend fun getShoeById(shoeId: Long): Shoes?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoe(shoe: Shoes): Long
    
    @Update
    suspend fun updateShoe(shoe: Shoes)
    
    @Delete
    suspend fun deleteShoe(shoe: Shoes)
} 