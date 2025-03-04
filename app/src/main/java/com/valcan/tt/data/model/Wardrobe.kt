package com.valcan.tt.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
@Entity(tableName = "wardrobes")
data class Wardrobe(
    @PrimaryKey(autoGenerate = true)
    val wardrobeId: Long = 0,
    val name: String,
    val description: String?,
    @kotlinx.serialization.Transient
    val createdAt: Date = Date()
) 