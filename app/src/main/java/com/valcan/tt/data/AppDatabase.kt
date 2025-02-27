package com.valcan.tt.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.valcan.tt.core.AISettings
import com.valcan.tt.data.converter.DateConverter
import com.valcan.tt.data.dao.ClothesDao
import com.valcan.tt.data.dao.ShoesDao
import com.valcan.tt.data.dao.UserDao
import com.valcan.tt.data.dao.WardrobeDao
import com.valcan.tt.data.model.Clothes
import com.valcan.tt.data.model.Shoes
import com.valcan.tt.data.model.User
import com.valcan.tt.data.model.Wardrobe

@Database(
    entities = [
        User::class,
        Wardrobe::class,
        Clothes::class,
        Shoes::class
    ],
    version = AISettings.DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun wardrobeDao(): WardrobeDao
    abstract fun clothesDao(): ClothesDao
    abstract fun shoesDao(): ShoesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    AISettings.DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 