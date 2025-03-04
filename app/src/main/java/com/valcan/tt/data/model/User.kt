package com.valcan.tt.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0,
    val name: String,
    val gender: String,
    @kotlinx.serialization.Transient
    val birthday: Date = Date(),
    @kotlinx.serialization.Transient
    val createdAt: Date = Date()
) 