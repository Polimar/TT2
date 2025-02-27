package com.valcan.tt.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clothes")
data class Clothes(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val season: String,
    val wardrobeId: Long? = null
) 