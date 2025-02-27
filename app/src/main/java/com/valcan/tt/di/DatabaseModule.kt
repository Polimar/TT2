package com.valcan.tt.di

import android.content.Context
import com.valcan.tt.data.AppDatabase
import com.valcan.tt.data.dao.ClothesDao
import com.valcan.tt.data.dao.ShoesDao
import com.valcan.tt.data.dao.UserDao
import com.valcan.tt.data.dao.WardrobeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }
    
    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
    
    @Provides
    fun provideWardrobeDao(database: AppDatabase): WardrobeDao {
        return database.wardrobeDao()
    }
    
    @Provides
    fun provideClothesDao(database: AppDatabase): ClothesDao {
        return database.clothesDao()
    }
    
    @Provides
    fun provideShoesDao(database: AppDatabase): ShoesDao {
        return database.shoesDao()
    }
} 