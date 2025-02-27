package com.valcan.tt.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "wardrobes")
data class Wardrobe(
    @PrimaryKey(autoGenerate = true)
    val wardrobeId: Long = 0,
    val name: String,
    val description: String?,
    val createdAt: Date = Date()
) 