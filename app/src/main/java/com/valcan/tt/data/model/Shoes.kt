package com.valcan.tt.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.util.Date

@Serializable
@Entity(
    tableName = "shoes",
    indices = [
        Index("wardrobeId"),
        Index("userId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = Wardrobe::class,
            parentColumns = ["wardrobeId"],
            childColumns = ["wardrobeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Shoes(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val wardrobeId: Long? = null,
    val userId: Long? = null,
    val color: String? = null,
    val type: String?,
    val season: String?,
    val imageUrl: String?,
    @Transient
    val createdAt: Date = Date()
) 