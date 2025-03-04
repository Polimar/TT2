package com.valcan.tt.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
@Entity(
    tableName = "clothes",
    indices = [
        Index("wardrobeId"),
        Index("userId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = Wardrobe::class,
            parentColumns = ["wardrobeId"],
            childColumns = ["wardrobeId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Clothes(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val color: String,
    val season: String,
    val position: String,
    val wardrobeId: Long? = null,
    val userId: Long? = null,
    val imageUrl: String? = null,
    @kotlinx.serialization.Transient
    val createdAt: Date = Date()
) 