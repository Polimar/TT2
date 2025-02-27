package com.valcan.tt.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Long = 0,
    val name: String,
    val gender: String,
    val birthday: Date,
    val createdAt: Date = Date()
) 